package genericdwh.gui.mainwindow.querypane.resultgrid;

import genericdwh.dataobjects.DataObject;
import genericdwh.dataobjects.dimension.Dimension;
import genericdwh.dataobjects.dimension.DimensionHierarchy;
import genericdwh.dataobjects.dimension.DimensionManager;
import genericdwh.dataobjects.ratio.Ratio;
import genericdwh.dataobjects.referenceobject.ReferenceObject;
import genericdwh.dataobjects.referenceobject.ReferenceObjectManager;
import genericdwh.gui.mainwindow.querypane.QueryPaneController.QueryType;
import genericdwh.main.Main;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;

import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import lombok.Getter;
import lombok.Setter;

public class ResultGrid extends GridPane {
	
	@Getter @Setter private int gridId = -1;
	
	@Getter private Ratio ratio;
	
	@Setter private String unitSymbol;

	private TreeMap<Long, ResultGridNode> rowHeaderTree;
	private TreeMap<Long, ResultGridNode> colHeaderTree;

	private int firstRowIndex;
	private int firstColIndex;

	private int colCount;
	private int rowCount;

	private Table<Integer, Integer, ResultGridNode> resultsTable = TreeBasedTable.create();

	private ArrayList<ResultGridNode> rowTotalHeaders;
	private ArrayList<ResultGridNode> colTotalHeaders;

	private Table<Integer, Integer, ResultGridCell> totalsTable = TreeBasedTable.create();
	private int grandTotalCol;
	private int grandTotalRow;
	
	@Getter private ArrayList<Entry<DimensionHierarchy, Integer>> hierarchies = new ArrayList<>();
	private boolean hasHierarchies = false;
	
	@Getter @Setter private List<DataObject> rowDims;
	@Getter @Setter private List<DataObject> colDims;
	@Getter @Setter private ArrayList<DataObject> combinedDims;
	@Getter @Setter private QueryType queryType;
	@Getter @Setter private List<DataObject> filter;

	public ResultGrid() {
		super();
		
		setPadding(new Insets(20));
	}

	private void initialize(Ratio ratio, ArrayList<TreeMap<Long, ReferenceObject>> rowRefObjs, ArrayList<TreeMap<Long, ReferenceObject>> colRefObjs, DimensionHierarchy changedHierarchy, Dimension newLevel) {
		this.ratio = ratio;
		setTitle(ratio.getName());
		
		rowHeaderTree = createHeaders(rowRefObjs, colRefObjs.size(), true, changedHierarchy, newLevel);
		colHeaderTree = createHeaders(colRefObjs, rowRefObjs.size(), false, changedHierarchy, newLevel);

		firstRowIndex = getFirstRowIndex(colRefObjs);
		firstColIndex = getFirstColIndex(rowRefObjs);

		rowCount = getTotalChildCount(rowHeaderTree);
		colCount = getTotalChildCount(colHeaderTree);

		createResultCells();
	}
	
	public void initializeDefault(Ratio ratio, ArrayList<TreeMap<Long, ReferenceObject>> rowRefObjs, ArrayList<TreeMap<Long, ReferenceObject>> colRefObjs) {
		initialize(ratio, rowRefObjs, colRefObjs, null, null);
	}
	
	public void initializeWTotals(Ratio ratio, ArrayList<TreeMap<Long, ReferenceObject>> rowRefObjs, ArrayList<TreeMap<Long, ReferenceObject>> colRefObjs) {
		initialize(ratio, rowRefObjs, colRefObjs, null, null);
		
		colTotalHeaders = createTotalHeaders(colRefObjs, rowRefObjs.size(), true);
		rowTotalHeaders = createTotalHeaders(rowRefObjs, colRefObjs.size(), false);

		createTotalCells();
	}
	
	public void initializeWHierarchiesWTotals(Ratio ratio, ArrayList<TreeMap<Long, ReferenceObject>> rowRefObjs, ArrayList<TreeMap<Long, ReferenceObject>> colRefObjs,
			List<DataObject> filter, List<DimensionHierarchy> hierarchies) {
		
		for(DimensionHierarchy hierarchy : hierarchies) {
			this.hierarchies.add(new SimpleEntry<>(hierarchy, 0));
		}
		hasHierarchies = true;
		
		initialize(ratio, rowRefObjs, colRefObjs, null, null);
				
		colTotalHeaders = createTotalHeaders(colRefObjs, rowRefObjs.size(), true);
		rowTotalHeaders = createTotalHeaders(rowRefObjs, colRefObjs.size(), false);

		createTotalCells();
	}
	
	public void reinitializeWHierarchiesWTotals(ArrayList<TreeMap<Long, ReferenceObject>> rowRefObjs, ArrayList<TreeMap<Long, ReferenceObject>> colRefObjs, DimensionHierarchy changedHierarchy, Dimension newLevel) {	
		getChildren().clear();
		resultsTable.clear();
		totalsTable.clear();
		
		initialize(ratio, rowRefObjs, colRefObjs, changedHierarchy, newLevel);
		
		colTotalHeaders = createTotalHeaders(colRefObjs, rowRefObjs.size(), true);
		rowTotalHeaders = createTotalHeaders(rowRefObjs, colRefObjs.size(), false);

		createTotalCells();
	}
	
	
	private void setTitle(String ratioName) {
		ResultGridCell title = new ResultGridCell(ratioName, 0, 0, 0);
		add(title, 0, 0);
	}
	
	
	private TreeMap<Long, ResultGridNode> createHeaders(ArrayList<TreeMap<Long, ReferenceObject>> refObjs, int baseOffset, boolean isRowHeader,
			DimensionHierarchy changedHierarchy, Dimension newLevel) {
		
		TreeMap<Long, ResultGridNode> root = new TreeMap<>();

		if (refObjs.isEmpty()) {
			return root;
		}

		if (baseOffset < 2) {
			baseOffset = 2;
		}
		
		for (ReferenceObject refObj : refObjs.get(0).values()) {
			ResultGridNode node = new ResultGridNode(refObj.getId(), refObj.getName(), refObj.getDimensionId(), refObj.getChildrenIds());
			root.put(node.getId(), node);
		}
		
		for (ResultGridNode rootNode : root.values()) {
			DimensionHierarchy currHierarchy = null;
			Entry<DimensionHierarchy, Integer> currHierarchyEntry = getHierarchy(rootNode.getDimensionId());
			if (currHierarchyEntry != null) {
				currHierarchy = currHierarchyEntry.getKey();
			}
			
			DimensionHierarchy prevHierarchy = currHierarchy;
			List<ResultGridNode> prevNodes = new ArrayList<>();
			prevNodes.add(rootNode);
			
			for (int i = 1; i < refObjs.size(); i++) {
				currHierarchy = null;
				currHierarchyEntry = getHierarchy(refObjs.get(i).firstEntry().getValue().getDimensionId());
				if (currHierarchyEntry != null) {
					currHierarchy = currHierarchyEntry.getKey();
				}
				
				List<ResultGridNode> newNodes = new ArrayList<>();
				for (ReferenceObject refObj : refObjs.get(i).values()) {
					ResultGridNode node = new ResultGridNode(refObj.getId(), refObj.getName(), refObj.getDimensionId(), refObj.getChildrenIds());					
					newNodes.add(node);
				}
				
				List<ResultGridNode> addedNodes = new ArrayList<>();
				if (!prevNodes.isEmpty()) {
					for (ResultGridNode prevNode : prevNodes) {
						if (currHierarchy == prevHierarchy && prevHierarchy != null) {
							if (prevNode.getSuccessorIds() != null) {
								for (long childId : prevNode.getSuccessorIds()) {
									for (ResultGridNode newNode : newNodes) {
										if (newNode.getId() == childId) {
											prevNode.addChild(newNode.getId(), newNode);
											addedNodes.add(newNode);
										}
									}
								}
							}
						} else {
							for (ResultGridNode newNode : newNodes) {
								ResultGridNode clone = newNode.cloneWOCell();
								prevNode.addChild(clone.getId(), clone);
								addedNodes.add(clone);
							}
						}
					}
				}
				prevHierarchy = currHierarchy;
				prevNodes = addedNodes;
			}
		}
		
		createHeaderCells(root, refObjs, baseOffset, isRowHeader);
		
		return root;
	}
	
	private void createHeaderCells(TreeMap<Long, ResultGridNode> root, ArrayList<TreeMap<Long, ReferenceObject>> refObjs, int baseOffset, boolean isRowHeader) {
		LinkedList<TreeMap<Long, ResultGridNode>> queue = new LinkedList<>();
		queue.add(root);
		
		int i = (refObjs.size() == 1) ? 1 : 0;
		int reset = 1;
		int counter = 0;
		int newOffset = baseOffset;
		int newReset = 0;
		while (!queue.isEmpty()) {
			TreeMap<Long, ResultGridNode> currBranch = queue.pop();
			
			int j;
			if (counter++ == reset) {
				j = baseOffset;
			} else {
				j = newOffset;
			}
			
			DimensionHierarchy currHierarchy = null;
			boolean collapsed = true;
			Dimension currLevel = null;
			boolean isLastLevel = true;
			if (hasHierarchies && currBranch.firstEntry() != null) {
				long dimId = currBranch.firstEntry().getValue().getDimensionId();
				
				Entry<DimensionHierarchy, Integer> currHierarchyEntry = getHierarchy(dimId);				
				if (currHierarchyEntry != null) {
					currHierarchy = currHierarchyEntry.getKey();
					int currMaxLevelNo = currHierarchyEntry.getValue();
					
					Entry<Integer, Dimension> currLevelEntry = getLevel(currHierarchy, dimId);
					int currLevelNo = currLevelEntry.getKey();
					currLevel = currLevelEntry.getValue();
					
					if (currLevelNo + 1 < currHierarchy.getLevels().size()) {
						isLastLevel = false;
						if (currMaxLevelNo > currLevelNo) {
							collapsed = false;
						}
					}	
				}
			}

			for (ResultGridNode currNode : currBranch.values()) {
				queue.add(currNode.getChildren());
				newReset ++;
								
				int childCount = getChildCount(currNode);
				ResultGridCell newCell = null;
				if (isRowHeader) {
					if (currHierarchy != null && !isLastLevel && currNode.getSuccessorIds() != null) {
						newCell = new ResultGridCell(currNode.getName(), i, j, 1, 1, childCount, currHierarchy, currLevel, collapsed);
					} else {
						newCell = new ResultGridCell(currNode.getName(), i, j, 1, 1, childCount);
					}
					ResultGrid.setRowSpan(newCell, childCount);
					add(newCell, i, j);
				} else {
					if (currHierarchy != null && !isLastLevel && currNode.getSuccessorIds() != null) {
						newCell = new ResultGridCell(currNode.getName(), j, i, 1, childCount, 1, currHierarchy, currLevel, collapsed) ;
					} else {
						newCell = new ResultGridCell(currNode.getName(), j, i, 1, childCount, 1);
					}
					ResultGrid.setColumnSpan(newCell, childCount);
					add(newCell, j, i);
				}
				currNode.setCell(newCell);
				
				j += childCount;
				newOffset = j;
			}
						
			if (counter == reset) {
				i++;
				
				reset = newReset;
				newReset = 0;
				counter = 0;
				newOffset = baseOffset;
			}
		}
	}

	
	private ArrayList<ResultGridNode> createTotalHeaders(ArrayList<TreeMap<Long, ReferenceObject>> refObjs, int offset, boolean isRowHeader) {
		ArrayList<ResultGridNode> headers = new ArrayList<>();
		
		if (refObjs.isEmpty()) {
			return headers;
		}

		if (offset == 0) {
			offset = 1;
		} else if (offset > 1) {
			offset--;
		}

		int span = (refObjs.size() > 0) ? refObjs.size() : 1;
		if (isRowHeader) {
			int colIndex = offset - 1;
			int rowIndex = firstRowIndex + rowCount;
			ResultGridCell totalsHeader = new ResultGridCell("Subtotals", colIndex, rowIndex, 2);
			add(totalsHeader, colIndex, rowIndex);
			ResultGrid.setRowSpan(totalsHeader, span);
		} else {
			int colIndex = firstColIndex + colCount;
			int rowIndex = offset - 1;
			ResultGridCell totalsHeader = new ResultGridCell("Subtotals", colIndex, rowIndex, 2);
			add(totalsHeader, colIndex, rowIndex);
			ResultGrid.setColumnSpan(totalsHeader, span);
		}

		DimensionManager dimManager = Main.getContext().getBean(DimensionManager.class);
		ReferenceObjectManager refObjManager = Main.getContext().getBean(ReferenceObjectManager.class);

		for (int i = refObjs.size(), j = 0; i > 0; i--, j++) {
			TreeMap<Long, ReferenceObject> currMap = refObjs.get(i - 1);
			Dimension dim = dimManager.getDimension(currMap.firstEntry().getValue().getDimensionId());
			ResultGridNode newNode;
			int colIndex;
			int rowIndex;
			if (isRowHeader) {
				colIndex = offset;
				rowIndex = firstRowIndex + rowCount + j;
			} else {
				colIndex = firstColIndex + colCount + j;
				rowIndex = offset;
			}
			newNode = new ResultGridNode(dim.getId(), new ResultGridCell(dim.getName(), colIndex, rowIndex, 2));
			newNode.setComponentIds(refObjManager.readRefObjIds(new ArrayList<>(currMap.values())));
			add(newNode.getCell(), colIndex, rowIndex);
			headers.add(newNode);
		}

		return headers;
	}
	

	private void createResultCells() {
		for (int i = firstRowIndex; i < firstRowIndex + rowCount; i++) {
			for (int j = firstColIndex; j < firstColIndex + colCount; j++) {
				ResultGridNode newNode = new ResultGridNode(0, new ResultGridCell("no data", j, i, 3));
				add(newNode.getCell(), j, i);
				resultsTable.put(i, j, newNode);
			}
		}
	}
	
	private void createTotalCells() {
		for (ResultGridNode currTotalHeader : colTotalHeaders) {
			int rowIndex = currTotalHeader.getCell().getRowIndex();
			
			ArrayList<ResultGridNode> headerNodes = getHeaderNodesByDimensionId(colHeaderTree, currTotalHeader.getId());
			
			for (int i = firstColIndex, j = 0; i < firstColIndex + colCount; j++) {
				
				ResultGridCell newCell = new ResultGridCell("no data", i, rowIndex, 4);
				add(newCell, i, rowIndex);
				int span = 1;
				if (j < headerNodes.size()) {
					span = headerNodes.get(j).getCell().getColSpan();
				}
				ResultGrid.setColumnSpan(newCell, span);

				totalsTable.put(rowIndex, i, newCell);
				
				i += span;
			}
		}

		for (ResultGridNode currTotalHeader : rowTotalHeaders) {
			int colIndex = currTotalHeader.getCell().getColIndex();

			ArrayList<ResultGridNode> headerNodes = getHeaderNodesByDimensionId(rowHeaderTree, currTotalHeader.getId());

			for (int i = firstRowIndex, j = 0; i < firstRowIndex + rowCount; j++) {	
				ResultGridCell newCell = new ResultGridCell("no data", colIndex, i, 4);
				add(newCell, colIndex, i);
				int span = 1;
				if (j < headerNodes.size()) {
					span = headerNodes.get(j).getCell().getRowSpan();
				}
				ResultGrid.setRowSpan(newCell, span);
				totalsTable.put(i, colIndex, newCell);
				
				i += span;
			}
		}

		grandTotalCol = firstColIndex + colCount;
		grandTotalRow = firstRowIndex + rowCount;

		ResultGridCell grandTotalCell = new ResultGridCell("no data", grandTotalCol, grandTotalRow, 5);
		add(grandTotalCell, grandTotalCol, grandTotalRow);
		ResultGrid.setColumnSpan(grandTotalCell, (rowTotalHeaders.size() > 0) ? rowTotalHeaders.size() : 1);
		ResultGrid.setRowSpan(grandTotalCell, (colTotalHeaders.size() > 0) ? colTotalHeaders.size() : 1);
		totalsTable.put(grandTotalRow, grandTotalCol, grandTotalCell);
	}
	
	
	public void postResult(Long id, Long[] componentIds, Double value) {
		int[] index = getCellIndex(componentIds);
		ResultGridNode node = resultsTable.get(index[0], index[1]);
		node.setId(id);
		node.setComponentIds(componentIds);
		node.getCell().setValue(value, unitSymbol);
	}
		
	public void calculateAndPostTotals() {
		calculateAndPostSubtotals(colTotalHeaders, true);
		calculateAndPostSubtotals(rowTotalHeaders, false);
		calculateAndPostGrandTotal();
	}
	
	private void calculateAndPostGrandTotal() {
		double total = 0;
		for (ResultGridNode resultNode : resultsTable.values()) {
			total += resultNode.getCell().getValue();
		}
		totalsTable.get(grandTotalRow, grandTotalCol).setValue(total, unitSymbol);
	}
	
	private void calculateAndPostSubtotals(ArrayList<ResultGridNode> headers, boolean isColTotalHeader) {		
		LinkedList<ResultGridNode> queue = new LinkedList<>();
		for (ResultGridNode currTotal : headers) {
			Long[] componentIds = currTotal.getComponentIds();
			
			if (isColTotalHeader) {
				queue.addAll(colHeaderTree.values());
				while (!queue.isEmpty()) {
					ResultGridNode currNode = queue.pop();
 					for (long component : componentIds) {
						ResultGridNode currHeaderNode = getHeaderNodeById(currNode, component);
						if (currHeaderNode != null) {
							double total = 0;
							int colStartIndex = currHeaderNode.getCell().getColIndex();
							int colEndIndex = colStartIndex + getTotalChildCountOld(currHeaderNode);
							for (int i = colStartIndex; i < colEndIndex; i++) {
								for (ResultGridNode resultNode : resultsTable.column(i).values()) {
									total += resultNode.getCell().getValue();
								}
								totalsTable.get(currTotal.getCell().getRowIndex(), currHeaderNode.getCell().getColIndex()).setValue(total, unitSymbol);
							}
						}
					}
				}
			} else {
				queue.addAll(rowHeaderTree.values());
				while (!queue.isEmpty()) {
					ResultGridNode currNode = queue.pop();
					queue.addAll(currNode.getChildren().values());
					for (long component : componentIds) {
						ResultGridNode currHeaderNode = getHeaderNodeById(currNode, component);
						if (currHeaderNode != null) {
							double total = 0;
							int rowStartIndex = currHeaderNode.getCell().getRowIndex();
							int rowEndIndex = rowStartIndex + getTotalChildCountOld(currHeaderNode);
							for (int i = rowStartIndex; i < rowEndIndex; i++) {
								for (ResultGridNode resultNode : resultsTable.row(i).values()) {
									total += resultNode.getCell().getValue();
								}
								ResultGridCell cell = totalsTable.get(currHeaderNode.getCell().getRowIndex(), currTotal.getCell().getColIndex());
								if (cell != null) {
									cell.setValue(total, unitSymbol);
								}
							}
						}
					}
				}
			}
		}
	}
	
	
	private int getTotalChildCount(TreeMap<Long, ResultGridNode> refObjs) {
		int count = 0;
		for (ResultGridNode currNode : refObjs.values()) {
			count += getChildCount(currNode);
		}
		if (count == 0) {
			return 1;
		}
		return count;
	}
	
	private int getChildCount(ResultGridNode rootNode) {
		int count = 0;
		LinkedList<ResultGridNode> queue = new LinkedList<>();
		queue.add(rootNode);
		while (!queue.isEmpty()) {
			ResultGridNode currNode = queue.pop();
			queue.addAll(currNode.getChildren().values());
			if (currNode.isLeaf()) {
				count++;
			}
		}
		return count;
	}
	
	
	private Entry<DimensionHierarchy, Integer> getHierarchy(long dimId) {
		for (Entry<DimensionHierarchy, Integer> currEntry : hierarchies) {
			for (Dimension level : currEntry.getKey().getLevels())
			if (level.getId() == dimId)  {
				return currEntry;
			}
		}
		
		return null;
	}
	
	private Entry<Integer, Dimension> getLevel(DimensionHierarchy hierarchy, long dimId) {
		LinkedList<Dimension> levels = hierarchy.getLevels();
		for (Dimension level : levels) {
			if (level.getId() == dimId) {
				return new SimpleEntry<>(levels.indexOf(level), level);
			}
		}
		
		return null;
	}
	

	private int[] getCellIndex(Long[] componentIds) {
		return new int[] { getRowIndex(componentIds), getColIndex(componentIds) };
	}
	
	private int getRowIndex(Long[] componentIds) {
		if (rowHeaderTree.isEmpty()) {
			return firstRowIndex;
		}
		
		LinkedList<ResultGridNode> queue = new LinkedList<>(rowHeaderTree.values());
		while (!queue.isEmpty()) {
			ResultGridNode currNode = queue.pop();
			for (long componentId : componentIds) {
				if (currNode.getId() == componentId) {
					if (currNode.isLeaf()) {
						return currNode.getCell().getRowIndex();
					}
					for (ResultGridNode currChild : currNode.getChildren().values()) {
						queue.push(currChild);
					}
					break;
				}
			}
			
		}
		
		return -1;
	}
	
	private int getColIndex(Long[] componentIds) {
		if (colHeaderTree.isEmpty()) {
			return firstColIndex;
		}
		
		LinkedList<ResultGridNode> queue = new LinkedList<>(colHeaderTree.values());
		while (!queue.isEmpty()) {
			ResultGridNode currNode = queue.pop();
			for (long componentId : componentIds) {
				if (currNode.getId() == componentId) {
					if (currNode.isLeaf()) {
						return currNode.getCell().getColIndex();
					}
					for (ResultGridNode currChild : currNode.getChildren().values()) {
						queue.push(currChild);
					}					
					break;
				}
			}
			
		}
		
		return -1;
	}
	
	private int getFirstRowIndex(ArrayList<TreeMap<Long, ReferenceObject>> colRefObjs) {
		if (colRefObjs.size() < 2) {
			return 2;
		} else {
			return colRefObjs.size();
		}
	}
	
	private int getFirstColIndex(ArrayList<TreeMap<Long, ReferenceObject>> rowRefObjs) {
		if (rowRefObjs.size() < 2) {
			return 2;
		} else {
			return rowRefObjs.size();
		}
	}
		

	private ResultGridNode getHeaderNodeById(ResultGridNode root, long id) {
		LinkedList<ResultGridNode> queue = new LinkedList<>();
		queue.add(root);
		while (!queue.isEmpty()) {
			ResultGridNode currBranch = queue.pop();
			if (currBranch.getId() == id) {
				return currBranch;
			} else {
				queue.addAll(currBranch.getChildren().values());
			}
		}
		return null;
	}
	
	private ArrayList<ResultGridNode> getHeaderNodesByDimensionId(TreeMap<Long, ResultGridNode> root, long dimId) {
		ArrayList<ResultGridNode> currNodes = new ArrayList<>();
		currNodes.addAll(root.values());
		
		while(true) {
			if (currNodes.get(0).getDimensionId() == dimId) {
				return currNodes;
			} else {
				ArrayList<ResultGridNode> tmpNodes = new ArrayList<>();
				for (ResultGridNode currNode : currNodes) {
					tmpNodes.addAll(currNode.getChildren().values());
				}
				currNodes = tmpNodes;
			}
		}
	}

	
	private int getTotalChildCountOld(ResultGridNode node) {
		ResultGridNode currNode = node;
		int count = 1;
		while (true) {
			if (!currNode.isLeaf()) {
				count *= currNode.getChildren().size();
				currNode = currNode.getChildren().firstEntry().getValue();
			} else {
				return count;
			}
		}
	}
}
