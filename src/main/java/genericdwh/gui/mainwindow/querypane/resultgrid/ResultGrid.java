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

		rowCount = getRefObjCount(rowRefObjs, 0);
		colCount = getRefObjCount(colRefObjs, 0);

		setHeaderSpans(rowHeaderTree, true);
		setHeaderSpans(colHeaderTree, false);

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
	
	public void initializeWHierarchiesWTotals(Ratio ratio, ArrayList<TreeMap<Long, ReferenceObject>> rowRefObjs, ArrayList<TreeMap<Long, ReferenceObject>> colRefObjs, ArrayList<DimensionHierarchy> hierarchies) {
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
		ResultGridCell title = new ResultGridCell(ratioName, 0, 0);
		add(title, 0, 0);
	}

	
	private TreeMap<Long, ResultGridNode> createHeaders(ArrayList<TreeMap<Long, ReferenceObject>> refObjs, int baseOffset, boolean isRowHeader, DimensionHierarchy changedHierarchy, Dimension newLevel) {
		TreeMap<Long, ResultGridNode> root = new TreeMap<>();

		if (refObjs.isEmpty()) {
			return root;
		}

		if (baseOffset < 2) {
			baseOffset = 2;
		}

		LinkedList<ResultGridNode> queue = new LinkedList<>();
		LinkedList<ResultGridNode> tmpQueue = new LinkedList<>();

		int initOffset = (refObjs.size() == 1) ? 1 : 0;

		int offset = baseOffset;
		int refObjCount = getRefObjCount(refObjs, 1);
		
		DimensionHierarchy currHierarchy = null;
		boolean collapsed = true;
		if (hasHierarchies) {
			Entry<DimensionHierarchy, Integer> currHierarchyEntry = getCurrentHierarchy(refObjs.get(0).firstEntry().getValue().getDimensionId());
			if (currHierarchyEntry != null) {
				currHierarchy = currHierarchyEntry.getKey();
				int currMaxLevelNo = currHierarchyEntry.getValue();
				
				if (currMaxLevelNo > 0) {
					collapsed = false;
				}
			}
		}
		
		for (Entry<Long, ReferenceObject> rootEntry : refObjs.get(0).entrySet()) {
			int j = offset;

			ResultGridNode newRootNode;
			if (isRowHeader) {
				if (currHierarchy != null) {
					newRootNode = new ResultGridNode(rootEntry.getKey(),
									new ResultGridCell(rootEntry.getValue().getName(), initOffset, j, currHierarchy, currHierarchy.getTopLevel(), collapsed));
				} else {
					newRootNode = new ResultGridNode(rootEntry.getKey(),
									new ResultGridCell(rootEntry.getValue().getName(), initOffset, j));
				}
				add(newRootNode.getCell(), initOffset, j);
			} else {
				if (currHierarchy != null) {
					newRootNode = new ResultGridNode(rootEntry.getKey(),
									new ResultGridCell(rootEntry.getValue().getName(), j, initOffset, currHierarchy, currHierarchy.getTopLevel(), collapsed));
				} else {
					newRootNode = new ResultGridNode(rootEntry.getKey(),
									new ResultGridCell(rootEntry.getValue().getName(), j, initOffset));
				}
				add(newRootNode.getCell(), j, initOffset);
			}
			newRootNode.setDimensionId(rootEntry.getValue().getDimensionId());

			if (refObjs.size() > 1) {
				offset += refObjCount;
			} else {
				offset++;
			}

			root.put(rootEntry.getKey(), newRootNode);

			queue.add(newRootNode);
		}

		for (int i = 1 + initOffset; i < refObjs.size(); i++) {
			currHierarchy = null;
			collapsed = true;
			Dimension currLevel = null;
			boolean isLastLevel = true;
			if (hasHierarchies) {
				long dimId = refObjs.get(i).firstEntry().getValue().getDimensionId();
				
				Entry<DimensionHierarchy, Integer> currHierarchyEntry = getCurrentHierarchy(dimId);				
				if (currHierarchyEntry != null) {
					currHierarchy = currHierarchyEntry.getKey();
					int currMaxLevelNo = currHierarchyEntry.getValue();
					
					Entry<Integer, Dimension> currLevelEntry = getCurrentLevel(currHierarchy, dimId);
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
			
			offset = baseOffset;
			refObjCount = getRefObjCount(refObjs, i);
			while (!queue.isEmpty()) {
				ResultGridNode currNode = queue.pop();
								
				int j = offset;
				for (Entry<Long, ReferenceObject> currEntry : refObjs.get(i).entrySet()) {
					ResultGridNode newNode;
					if (isRowHeader) {
						if (currHierarchy != null && !isLastLevel) {
							newNode = new ResultGridNode(currEntry.getKey(),
										new ResultGridCell(currEntry.getValue().getName(), i, j, currHierarchy, currLevel, collapsed));
						}
						else {
							newNode = new ResultGridNode(currEntry.getKey(),
										new ResultGridCell(currEntry.getValue().getName(), i, j));
						}
						add(newNode.getCell(), i, j);
					} else {
						if (currHierarchy != null && !isLastLevel) {
							newNode = new ResultGridNode(currEntry.getKey(),
										new ResultGridCell(currEntry.getValue().getName(), j, i, currHierarchy, currLevel, collapsed));
						} else {
							newNode = new ResultGridNode(currEntry.getKey(),
										new ResultGridCell(currEntry.getValue().getName(), j, i));
						}
						add(newNode.getCell(), j, i);
					}
					newNode.setDimensionId(currEntry.getValue().getDimensionId());

					if (refObjs.size() > i + 1) {
						j += refObjs.get(i + 1).size();
					} else {
						j++;
					}
					currNode.addChild(currEntry.getValue().getId(), newNode);
					tmpQueue.add(newNode);
				}

				if (refObjs.size() > i) {
					offset += refObjCount;
				} else {
					offset++;
				}
			}
			queue.addAll(tmpQueue);
			tmpQueue.clear();
		}
		return root;
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
			int rowIndex = firstRowIndex + rowCount + 1;
			ResultGridCell totalsHeader = new ResultGridCell("Subtotals", colIndex, rowIndex);
			add(totalsHeader, colIndex, rowIndex);
			ResultGrid.setRowSpan(totalsHeader, span);
		} else {
			int colIndex = firstColIndex + colCount + 1;
			int rowIndex = offset - 1;
			ResultGridCell totalsHeader = new ResultGridCell("Subtotals", colIndex, rowIndex);
			add(totalsHeader, colIndex, rowIndex);
			ResultGrid.setColumnSpan(totalsHeader, span);
		}

		DimensionManager dimManager = Main.getContext().getBean(DimensionManager.class);
		ReferenceObjectManager refObjManager = Main.getContext().getBean(ReferenceObjectManager.class);

		for (int i = refObjs.size(), j = 1; i > 0; i--, j++) {
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
			newNode = new ResultGridNode(dim.getId(), new ResultGridCell(dim.getName(), colIndex, rowIndex));
			newNode.setComponentIds(refObjManager.readRefObjComponentIds(new ArrayList<>(currMap.values())));
			add(newNode.getCell(), colIndex, rowIndex);
			headers.add(newNode);
		}

		return headers;
	}

	
	private void createResultCells() {
		for (int i = firstRowIndex; i < firstRowIndex + rowCount; i++) {
			for (int j = firstColIndex; j < firstColIndex + colCount; j++) {
				ResultGridNode newNode = new ResultGridNode(0, new ResultGridCell(j, i));
				add(newNode.getCell(), j, i);
				resultsTable.put(i, j, newNode);
			}
		}
	}

	private void createTotalCells() {
		for (ResultGridNode currTotalHeader : colTotalHeaders) {
			int rowIndex = currTotalHeader.getCell().getRowIndex();

			int span = getHeaderNodeByDimensionId(colHeaderTree.firstEntry().getValue(), currTotalHeader.getId()).getCell().getColSpan();
			for (int i = firstColIndex; i < firstColIndex + colCount; i += span) {
				ResultGridCell newCell = new ResultGridCell(i, rowIndex);
				add(newCell, i, rowIndex);
				ResultGrid.setColumnSpan(newCell, span);
				totalsTable.put(rowIndex, i, newCell);
			}
		}

		for (ResultGridNode currTotalHeader : rowTotalHeaders) {
			int colIndex = currTotalHeader.getCell().getColIndex();

			int span = getHeaderNodeByDimensionId(rowHeaderTree.firstEntry().getValue(), currTotalHeader.getId()).getCell().getRowSpan();
			for (int i = firstRowIndex; i < firstRowIndex + rowCount; i += span) {
				ResultGridCell newCell = new ResultGridCell(colIndex, i);
				add(newCell, colIndex, i);
				ResultGrid.setRowSpan(newCell, span);
				totalsTable.put(i, colIndex, newCell);
			}
		}

		grandTotalCol = firstColIndex + colCount + 1;
		grandTotalRow = firstRowIndex + rowCount + 1;

		ResultGridCell grandTotalCell = new ResultGridCell(grandTotalCol, grandTotalRow);
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
					queue.addAll(currNode.getChildren().values());
					for (long component : componentIds) {
						ResultGridNode currHeaderNode = getHeaderNodeById(currNode, component);
						if (currHeaderNode != null) {
							double total = 0;
							int colStartIndex = currHeaderNode.getCell().getColIndex();
							int colEndIndex = colStartIndex + getTotalChildCount(currHeaderNode);
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
							int rowEndIndex = rowStartIndex + getTotalChildCount(currHeaderNode);
							for (int i = rowStartIndex; i < rowEndIndex; i++) {
								for (ResultGridNode resultNode : resultsTable.row(i).values()) {
									total += resultNode.getCell().getValue();
								}
								totalsTable.get(currHeaderNode.getCell().getRowIndex(), currTotal.getCell().getColIndex()).setValue(total, unitSymbol);
							}
						}
					}
				}
			}
		}
	}

	
	private int[] getCellIndex(Long[] componentIds) {
		return new int[] { getRowIndex(componentIds), getColIndex(componentIds) };
	}
	
	private int getRowIndex(Long[] componentIds) {
		if (rowHeaderTree.isEmpty()) {
			return firstRowIndex;
		}

		TreeMap<Long, ResultGridNode> currNodes = rowHeaderTree;
		int index = -1;
		ResultGridNode lastMatch = null;
		while (index == -1) {
			ResultGridNode foundNode = null;
			for (long componentId : componentIds) {
				foundNode = currNodes.get(componentId);
				if (foundNode != null) {
					lastMatch = foundNode;
					if (foundNode.isLeaf()) {
						index = foundNode.getCell().getRowIndex();
					} else {
						currNodes = foundNode.getChildren();
					}
					break;
				}
			}
			if (foundNode == null && lastMatch != null) {
				index = lastMatch.getCell().getRowIndex();
			}
		}

		return index;
	}

	private int getColIndex(Long[] componentIds) {
		if (colHeaderTree.isEmpty()) {
			return firstColIndex;
		}

		TreeMap<Long, ResultGridNode> currNodes = colHeaderTree;
		int index = -1;
		ResultGridNode lastMatch = null;
		while (index == -1) {
			ResultGridNode foundNode = null;
			for (long componentId : componentIds) {
				foundNode = currNodes.get(componentId);
				if (foundNode != null) {
					lastMatch = foundNode;
					if (foundNode.isLeaf()) {
						index = foundNode.getCell().getColIndex();
					} else {
						currNodes = foundNode.getChildren();
					}
					break;
				}
			}

			if (foundNode == null && lastMatch != null) {
				index = lastMatch.getCell().getColIndex();
			}
		}

		return index;
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

	
	private void setHeaderSpans(TreeMap<Long, ResultGridNode> headerTree, boolean isRowHeader) {
		LinkedList<ResultGridNode> queue = new LinkedList<>(headerTree.values());
		
		while (!queue.isEmpty()) {
			ResultGridNode currNode = queue.pop();
			if (isRowHeader) {
				int span = getTotalChildCount(currNode);
				ResultGrid.setRowSpan(currNode.getCell(), span);
				currNode.getCell().setRowSpan(span);
			} else {
				int span = getTotalChildCount(currNode);
				ResultGrid.setColumnSpan(currNode.getCell(), span);
				currNode.getCell().setColSpan(span);
			}

			for (ResultGridNode currChild : currNode.getChildren().values()) {
				queue.add(currChild);
			}
		}
	}

	
	private int getRefObjCount(ArrayList<TreeMap<Long, ReferenceObject>> refObjs, int startIndex) {
		int count = 1;
		for (int i = startIndex; i < refObjs.size(); i++) {
			count *= refObjs.get(i).size();
		}
		return count;
	}
	
	private int getTotalChildCount(ResultGridNode node) {
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

	private ResultGridNode getHeaderNodeByDimensionId(ResultGridNode root, long dimensionId) {
		LinkedList<ResultGridNode> queue = new LinkedList<>();
		queue.add(root);
		while (!queue.isEmpty()) {
			ResultGridNode currBranch = queue.pop();
			if (currBranch.getDimensionId() == dimensionId) {
				return currBranch;
			} else {
				queue.addAll(currBranch.getChildren().values());
			}
		}
		return null;
	}

	
	private Entry<DimensionHierarchy, Integer> getCurrentHierarchy(long dimId) {
		for (Entry<DimensionHierarchy, Integer> currEntry : hierarchies) {
			for (Dimension level : currEntry.getKey().getLevels())
			if (level.getId() == dimId)  {
				return currEntry;
			}
		}
		
		return null;
	}
	
	private Entry<Integer, Dimension> getCurrentLevel(DimensionHierarchy hierarchy, long dimId) {
		LinkedList<Dimension> levels = hierarchy.getLevels();
		for (Dimension level : levels) {
			if (level.getId() == dimId) {
				return new SimpleEntry<>(levels.indexOf(level), level);
			}
		}
		
		return null;
	}
}
