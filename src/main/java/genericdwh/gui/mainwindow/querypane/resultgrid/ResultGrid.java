package genericdwh.gui.mainwindow.querypane.resultgrid;

import genericdwh.dataobjects.DataObject;
import genericdwh.dataobjects.dimension.Dimension;
import genericdwh.dataobjects.dimension.DimensionHierarchy;
import genericdwh.dataobjects.dimension.DimensionManager;
import genericdwh.dataobjects.ratio.Ratio;
import genericdwh.dataobjects.referenceobject.ReferenceObject;
import genericdwh.dataobjects.referenceobject.ReferenceObjectManager;
import genericdwh.gui.mainwindow.querypane.QueryPaneController.AggregationType;
import genericdwh.gui.mainwindow.querypane.QueryPaneController.QueryType;
import genericdwh.main.Main;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
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

	private ArrayList<ResultGridNode> rowAggregationHeaders;
	private ArrayList<ResultGridNode> colAggregationHeaders;

	private AggregationType aggregationType;
	private Table<Integer, Integer, ResultGridCell> aggregationsTable = TreeBasedTable.create();
	private int totalAggregationCol;
	private int totalAggregationRow;
	
	@Getter private ArrayList<Entry<DimensionHierarchy, Integer>> hierarchies = new ArrayList<>();
	private boolean hasHierarchies = false;
	
	@Getter @Setter private List<DataObject> rowDims;
	@Getter @Setter private List<DataObject> colDims;
	@Getter @Setter private List<DataObject> combinedDims;
	@Getter @Setter private QueryType queryType;
	@Getter @Setter private List<DataObject> filter;
	
	@Getter @Setter private String query;

	public ResultGrid() {
		super();
		
		setPadding(new Insets(20));
	}

	private void initialize(Ratio ratio, List<TreeMap<Long, ReferenceObject>> rowRefObjs, List<TreeMap<Long, ReferenceObject>> colRefObjs, DimensionHierarchy changedHierarchy, Dimension newLevel) {
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
	
	public void initializeDefault(Ratio ratio, List<TreeMap<Long, ReferenceObject>> rowRefObjs, List<TreeMap<Long, ReferenceObject>> colRefObjs) {
		initialize(ratio, rowRefObjs, colRefObjs, null, null);
	}
	
	public void initializeWAggregations(Ratio ratio, List<TreeMap<Long, ReferenceObject>> rowRefObjs, List<TreeMap<Long, ReferenceObject>> colRefObjs,
			AggregationType aggregationType) {
		
		initialize(ratio, rowRefObjs, colRefObjs, null, null);
		
		this.aggregationType = aggregationType;
		colAggregationHeaders = createAggregationHeaders(colRefObjs, rowRefObjs.size(), true);
		rowAggregationHeaders = createAggregationHeaders(rowRefObjs, colRefObjs.size(), false);

		createAggregationCells();
	}
	
	public void initializeWHierarchiesWAggregations(Ratio ratio, List<TreeMap<Long, ReferenceObject>> rowRefObjs, List<TreeMap<Long, ReferenceObject>> colRefObjs,
			List<DataObject> filter, List<DimensionHierarchy> hierarchies,
			AggregationType aggregationType) {
		
		for(DimensionHierarchy hierarchy : hierarchies) {
			this.hierarchies.add(new SimpleEntry<>(hierarchy, 0));
		}
		hasHierarchies = true;
		
		initialize(ratio, rowRefObjs, colRefObjs, null, null);
		
		this.aggregationType = aggregationType;
		colAggregationHeaders = createAggregationHeaders(colRefObjs, rowRefObjs.size(), true);
		rowAggregationHeaders = createAggregationHeaders(rowRefObjs, colRefObjs.size(), false);

		createAggregationCells();
	}
	
	public void reinitializeWHierarchiesWAggregations(ArrayList<TreeMap<Long, ReferenceObject>> rowRefObjs, ArrayList<TreeMap<Long, ReferenceObject>> colRefObjs,
			DimensionHierarchy changedHierarchy, Dimension newLevel) {	
		
		getChildren().clear();
		resultsTable.clear();
		aggregationsTable.clear();
		
		initialize(ratio, rowRefObjs, colRefObjs, changedHierarchy, newLevel);
		
		colAggregationHeaders = createAggregationHeaders(colRefObjs, rowRefObjs.size(), true);
		rowAggregationHeaders = createAggregationHeaders(rowRefObjs, colRefObjs.size(), false);

		createAggregationCells();
	}
	
	
	private void setTitle(String ratioName) {
		ResultGridCell title = new ResultGridCell(ratioName, 0, 0, 0);
		add(title, 0, 0);
	}
	
	
	private TreeMap<Long, ResultGridNode> createHeaders(List<TreeMap<Long, ReferenceObject>> refObjs, int baseOffset, boolean isRowHeader,
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
	
	private void createHeaderCells(TreeMap<Long, ResultGridNode> root, List<TreeMap<Long, ReferenceObject>> refObjs, int baseOffset, boolean isRowHeader) {
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

	
	private ArrayList<ResultGridNode> createAggregationHeaders(List<TreeMap<Long, ReferenceObject>> refObjs, int offset, boolean isRowHeader) {
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
			ResultGridCell totalsHeader = new ResultGridCell(aggregationType.getName(), colIndex, rowIndex, 2);
			add(totalsHeader, colIndex, rowIndex);
			ResultGrid.setRowSpan(totalsHeader, span);
		} else {
			int colIndex = firstColIndex + colCount;
			int rowIndex = offset - 1;
			ResultGridCell totalsHeader = new ResultGridCell(aggregationType.getName(), colIndex, rowIndex, 2);
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
	
	private void createAggregationCells() {
		for (ResultGridNode currAggregationHeader : colAggregationHeaders) {
			int rowIndex = currAggregationHeader.getCell().getRowIndex();
			
			ArrayList<ResultGridNode> headerNodes = getHeaderNodesByDimensionId(colHeaderTree, currAggregationHeader.getId());
			
			for (int i = firstColIndex, j = 0; i < firstColIndex + colCount; j++) {
				
				ResultGridCell newCell = new ResultGridCell("no data", i, rowIndex, 4);
				add(newCell, i, rowIndex);
				int span = 1;
				if (j < headerNodes.size()) {
					span = headerNodes.get(j).getCell().getColSpan();
				}
				ResultGrid.setColumnSpan(newCell, span);

				aggregationsTable.put(rowIndex, i, newCell);
				
				i += span;
			}
		}

		for (ResultGridNode currAggregationHeader : rowAggregationHeaders) {
			int colIndex = currAggregationHeader.getCell().getColIndex();

			ArrayList<ResultGridNode> headerNodes = getHeaderNodesByDimensionId(rowHeaderTree, currAggregationHeader.getId());

			for (int i = firstRowIndex, j = 0; i < firstRowIndex + rowCount; j++) {	
				ResultGridCell newCell = new ResultGridCell("no data", colIndex, i, 4);
				add(newCell, colIndex, i);
				int span = 1;
				if (j < headerNodes.size()) {
					span = headerNodes.get(j).getCell().getRowSpan();
				}
				ResultGrid.setRowSpan(newCell, span);
				aggregationsTable.put(i, colIndex, newCell);
				
				i += span;
			}
		}

		totalAggregationCol = firstColIndex + colCount;
		totalAggregationRow = firstRowIndex + rowCount;

		ResultGridCell totalAggregationCell = new ResultGridCell("no data", totalAggregationCol, totalAggregationRow, 5);
		add(totalAggregationCell, totalAggregationCol, totalAggregationRow);
		ResultGrid.setColumnSpan(totalAggregationCell, (rowAggregationHeaders.size() > 0) ? rowAggregationHeaders.size() : 1);
		ResultGrid.setRowSpan(totalAggregationCell, (colAggregationHeaders.size() > 0) ? colAggregationHeaders.size() : 1);
		aggregationsTable.put(totalAggregationRow, totalAggregationCol, totalAggregationCell);
	}
	
	
	public void postResult(Long id, Long[] componentIds, Double value) {
		int[] index = getCellIndex(componentIds);
		ResultGridNode node = resultsTable.get(index[0], index[1]);
		node.setId(id);
		node.setComponentIds(componentIds);
		node.getCell().setValue(value, unitSymbol);
	}
		
	public void calculateAndPostAggregations() {
		calculateAndPostSubAggregations(colAggregationHeaders, true);
		calculateAndPostSubAggregations(rowAggregationHeaders, false);
		calculateAndPostTotalAggregation();
	}
	
	private void calculateAndPostTotalAggregation() {
		double aggregation = 0;
		switch (aggregationType) {
			case TOTALS: {
				for (ResultGridNode resultNode : resultsTable.values()) {
					if (resultNode.getCell().isData()) {
						aggregation += resultNode.getCell().getValue();
					}
				}
				break;
			}
			case MEANS: {
				int count = 0;
				for (ResultGridNode resultNode : resultsTable.values()) {
					if (resultNode.getCell().isData()) {
						aggregation += resultNode.getCell().getValue();
						count++;
					}
				}
				if (count == 0) {
					aggregation = 0;
					break;
				}
				
				aggregation /= count;
				break;
			}
		}
		aggregationsTable.get(totalAggregationRow, totalAggregationCol).setValue(round2Decimals(aggregation), unitSymbol);
	}
	
	private void calculateAndPostSubAggregations(ArrayList<ResultGridNode> headers, boolean isColTotalHeader) {		
		LinkedList<ResultGridNode> queue = new LinkedList<>();
		for (ResultGridNode currAggregation : headers) {
			Long[] componentIds = currAggregation.getComponentIds();
			
			if (isColTotalHeader) {
				queue.addAll(colHeaderTree.values());
				while (!queue.isEmpty()) {
					ResultGridNode currNode = queue.pop();
 					for (long component : componentIds) {
						ResultGridNode currHeaderNode = getHeaderNodeById(currNode, component);
						if (currHeaderNode != null) {
							double aggregation = 0;
							int colStartIndex = currHeaderNode.getCell().getColIndex();
							int colEndIndex = colStartIndex + getTotalChildCountOld(currHeaderNode);
							for (int i = colStartIndex; i < colEndIndex; i++) {
								ResultGridCell cell = aggregationsTable.get(currAggregation.getCell().getRowIndex(), currHeaderNode.getCell().getColIndex());
								if (cell != null) {
									switch (aggregationType) {
										case TOTALS: {
											aggregation += calculateTotal(isColTotalHeader, i);
											break;
										}
										case MEANS: {
											aggregation += calculateMean(isColTotalHeader, i);
											break;
										}
									}
									cell.setValue(round2Decimals(aggregation), unitSymbol);
								}
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
							double aggregation = 0;
							int rowStartIndex = currHeaderNode.getCell().getRowIndex();
							int rowEndIndex = rowStartIndex + getTotalChildCountOld(currHeaderNode);
							for (int i = rowStartIndex; i < rowEndIndex; i++) {
								ResultGridCell cell = aggregationsTable.get(currHeaderNode.getCell().getRowIndex(), currAggregation.getCell().getColIndex());
								if (cell != null) {
									switch (aggregationType) {
									case TOTALS: {
										aggregation += calculateTotal(isColTotalHeader, i);
										break;
									}
									case MEANS: {
										aggregation += calculateMean(isColTotalHeader, i);
										break;
									}
								}
									cell.setValue(round2Decimals(aggregation), unitSymbol);
								}
							}
						}
					}
				}
			}
		}
	}
	
	private double round2Decimals(double value) {
		return (double)Math.round(value * 100) / 100;
	}
	
	private double calculateTotal(boolean isColTotalHeader, int currIndex) {
		double total = 0;
		Collection<ResultGridNode> nodes;
		if (isColTotalHeader) {
			nodes = resultsTable.column(currIndex).values();
		} else {
			nodes = resultsTable.row(currIndex).values();
		}
		for (ResultGridNode resultNode : nodes) {
			if (resultNode.getCell().isData()) {
				total += resultNode.getCell().getValue();
			}
		}
		
		return total;
	}
	
	private double calculateMean(boolean isColTotalHeader, int currIndex) {
		double mean = 0;
		Collection<ResultGridNode> nodes;
		if (isColTotalHeader) {
			nodes = resultsTable.column(currIndex).values();
		} else {
			nodes = resultsTable.row(currIndex).values();
		}
		int count = 0;
		for (ResultGridNode resultNode : nodes) {
			if (resultNode.getCell().isData()) {
				mean += resultNode.getCell().getValue();
				count++;
			}
		}
		
		if (count == 0) {
			return 0;
		}
		
		mean /= count;
		
		return mean;
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
	
	private int getFirstRowIndex(List<TreeMap<Long, ReferenceObject>> colRefObjs) {
		if (colRefObjs.size() < 2) {
			return 2;
		} else {
			return colRefObjs.size();
		}
	}
	
	private int getFirstColIndex(List<TreeMap<Long, ReferenceObject>> rowRefObjs) {
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
