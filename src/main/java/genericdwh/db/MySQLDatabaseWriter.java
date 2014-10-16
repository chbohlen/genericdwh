package genericdwh.db;

import static genericdwh.db.model.tables.DimensionCategories.DIMENSION_CATEGORIES;
import static genericdwh.db.model.tables.Dimensions.DIMENSIONS;
import static genericdwh.db.model.tables.DimensionCombinations.DIMENSION_COMBINATIONS;
import static genericdwh.db.model.tables.DimensionHierarchies.DIMENSION_HIERARCHIES;
import static genericdwh.db.model.tables.RatioCategories.RATIO_CATEGORIES;
import static genericdwh.db.model.tables.ReferenceObjects.REFERENCE_OBJECTS;
import static genericdwh.db.model.tables.ReferenceObjectCombinations.REFERENCE_OBJECT_COMBINATIONS;
import static genericdwh.db.model.tables.ReferenceObjectHierarchies.REFERENCE_OBJECT_HIERARCHIES;
import static genericdwh.db.model.tables.Ratios.RATIOS;
import static genericdwh.db.model.tables.RatioRelations.RATIO_RELATIONS;
import static genericdwh.db.model.tables.FactUnits.FACT_UNITS;
import static genericdwh.db.model.tables.Facts.FACTS;

import java.sql.Connection;
import java.util.List;

import genericdwh.dataobjects.dimension.Dimension;
import genericdwh.dataobjects.dimension.DimensionCategory;
import genericdwh.dataobjects.dimension.DimensionCombination;
import genericdwh.dataobjects.dimension.DimensionHierarchy;
import genericdwh.dataobjects.fact.Fact;
import genericdwh.dataobjects.ratio.Ratio;
import genericdwh.dataobjects.ratio.RatioCategory;
import genericdwh.dataobjects.ratio.RatioRelation;
import genericdwh.dataobjects.referenceobject.ReferenceObject;
import genericdwh.dataobjects.referenceobject.ReferenceObjectCombination;
import genericdwh.dataobjects.referenceobject.ReferenceObjectHierarchy;
import genericdwh.dataobjects.unit.Unit;
import genericdwh.db.model.tables.records.DimensionCategoriesRecord;
import genericdwh.db.model.tables.records.DimensionCombinationsRecord;
import genericdwh.db.model.tables.records.DimensionHierarchiesRecord;
import genericdwh.db.model.tables.records.DimensionsRecord;
import genericdwh.db.model.tables.records.FactUnitsRecord;
import genericdwh.db.model.tables.records.FactsRecord;
import genericdwh.db.model.tables.records.RatioCategoriesRecord;
import genericdwh.db.model.tables.records.RatioRelationsRecord;
import genericdwh.db.model.tables.records.RatiosRecord;
import genericdwh.db.model.tables.records.ReferenceObjectCombinationsRecord;
import genericdwh.db.model.tables.records.ReferenceObjectHierarchiesRecord;
import genericdwh.db.model.tables.records.ReferenceObjectsRecord;
import genericdwh.main.Main;
import lombok.Setter;

import org.jooq.DSLContext;
import org.jooq.DeleteConditionStep;
import org.jooq.InsertValuesStep1;
import org.jooq.InsertValuesStep2;
import org.jooq.InsertValuesStep4;
import org.jooq.UpdateConditionStep;
import org.jooq.conf.ParamType;
import org.jooq.exception.DataAccessException;

public class MySQLDatabaseWriter implements DatabaseWriter {
	
	@Setter private DSLContext dslContext;
	
	@Override
	public boolean createDimensions(List<Dimension> creations) {
		Connection con = null;
		try {
			con = dslContext.configuration().connectionProvider().acquire();
			con.setAutoCommit(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (Dimension dim : creations) {
			Long catId = dim.getCategoryProperty().get().getId() == 0 ? null : dim.getCategoryProperty().get().getId();
			InsertValuesStep2<DimensionsRecord, String, Long> statement = dslContext
																			.insertInto(DIMENSIONS, DIMENSIONS.NAME, DIMENSIONS.CATEGORY_ID)
																			.values(dim.getNameProperty().get(), catId);
			
			statement.execute();
			
			Main.getLogger().info("Dimension created.");
			Main.getLogger().info("Executed SQL: " + statement.getSQL(ParamType.INLINED));
		}
		
		try {
			con.setAutoCommit(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	@Override
	public boolean updateDimensions(List<Dimension> updates) {	
		Connection con = null;
		try {
			con = dslContext.configuration().connectionProvider().acquire();
			con.setAutoCommit(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (Dimension dim : updates) {
			Long catId = dim.getCategoryProperty().get().getId() == 0 ? null : dim.getCategoryProperty().get().getId();
			UpdateConditionStep<DimensionsRecord> statement = dslContext
																.update(DIMENSIONS)
																.set(DIMENSIONS.NAME, dim.getNameProperty().get())
																.set(DIMENSIONS.CATEGORY_ID, catId)
																.where(DIMENSIONS.DIMENSION_ID.equal(dim.getId()));
			
			statement.execute();
			
			Main.getLogger().info("Dimension updated.");
			Main.getLogger().info("Executed SQL: " + statement.getSQL(ParamType.INLINED));
		}
		
		try {
			con.setAutoCommit(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	@Override
	public boolean deleteDimensions(List<Dimension> deletions) {
		Connection con = null;
		try {
			con = dslContext.configuration().connectionProvider().acquire();
			con.setAutoCommit(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (Dimension dim : deletions) {
			DeleteConditionStep<DimensionsRecord> statement = dslContext
																.delete(DIMENSIONS)
																.where(DIMENSIONS.DIMENSION_ID.equal(dim.getId()));
			
			try {
				statement.execute();
			} catch (DataAccessException e) {
				Main.getLogger().info("Could not delete Dimension. Dimension possibly still referenced elsewhere.");
				Main.getLogger().info("Executed SQL: " + statement.getSQL(ParamType.INLINED));
				return false;
			}
			
			Main.getLogger().info("Dimension deleted.");
			Main.getLogger().info("Executed SQL: " + statement.getSQL(ParamType.INLINED));
		}
		
		try {
			con.setAutoCommit(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	
	@Override
	public boolean createReferenceObjects(List<ReferenceObject> creations) {
		Connection con = null;
		try {
			con = dslContext.configuration().connectionProvider().acquire();
			con.setAutoCommit(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (ReferenceObject refObj : creations) {
			InsertValuesStep2<ReferenceObjectsRecord, String, Long> statement = dslContext
																				.insertInto(REFERENCE_OBJECTS, REFERENCE_OBJECTS.NAME, REFERENCE_OBJECTS.DIMENSION_ID)
																				.values(refObj.getNameProperty().get(), refObj.getDimensionProperty().get().getId());
			
			statement.execute();
			
			Main.getLogger().info("Reference Object created.");
			Main.getLogger().info("Executed SQL: " + statement.getSQL(ParamType.INLINED));
		}
		
		try {
			con.setAutoCommit(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	@Override
	public boolean updateReferenceObjects(List<ReferenceObject> updates) {
		Connection con = null;
		try {
			con = dslContext.configuration().connectionProvider().acquire();
			con.setAutoCommit(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (ReferenceObject refObj : updates) {
			UpdateConditionStep<ReferenceObjectsRecord> statement = dslContext
																	.update(REFERENCE_OBJECTS)
																	.set(REFERENCE_OBJECTS.NAME, refObj.getNameProperty().get())
																	.set(REFERENCE_OBJECTS.DIMENSION_ID, refObj.getDimensionProperty().get().getId())
																	.where(REFERENCE_OBJECTS.REFERENCE_OBJECT_ID.equal(refObj.getId()));
			
			statement.execute();
			
			Main.getLogger().info("Reference Object updated.");
			Main.getLogger().info("Executed SQL: " + statement.getSQL(ParamType.INLINED));
			}
		
		try {
			con.setAutoCommit(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public boolean deleteReferenceObjects(List<ReferenceObject> deletions) {
		Connection con = null;
		try {
			con = dslContext.configuration().connectionProvider().acquire();
			con.setAutoCommit(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (ReferenceObject refObj : deletions) {
			DeleteConditionStep<ReferenceObjectsRecord> statement = dslContext
																	.delete(REFERENCE_OBJECTS)
																	.where(REFERENCE_OBJECTS.REFERENCE_OBJECT_ID.equal(refObj.getId()));
			
			try {
				statement.execute();
			} catch (DataAccessException e) {
				Main.getLogger().info("Could not delete Reference Object. Reference Object possibly still referenced elsewhere.");
				Main.getLogger().info("Executed SQL: " + statement.getSQL(ParamType.INLINED));
				return false;
			}
			
			Main.getLogger().info("Reference Object deleted.");
			Main.getLogger().info("Executed SQL: " + statement.getSQL(ParamType.INLINED));
		}
		
		try {
			con.setAutoCommit(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	
	@Override
	public boolean createDimensionHierarchies(List<DimensionHierarchy> creations) {
		Connection con = null;
		try {
			con = dslContext.configuration().connectionProvider().acquire();
			con.setAutoCommit(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (DimensionHierarchy hierarchy : creations) {
			List<Dimension> levels = hierarchy.getLevelsProperty().get();
			for (int i = 0; i < levels.size() - 1; i++) {
				InsertValuesStep2<DimensionHierarchiesRecord, Long, Long> statement = dslContext
																						.insertInto(DIMENSION_HIERARCHIES, DIMENSION_HIERARCHIES.PARENT_ID, DIMENSION_HIERARCHIES.CHILD_ID)
																						.values(levels.get(i).getId(), levels.get(i + 1).getId());
				
				statement.execute();
				
				Main.getLogger().info("Dimension Hierarchy created.");
				Main.getLogger().info("Executed SQL: " + statement.getSQL(ParamType.INLINED));
			}
		}
		
		try {
			con.setAutoCommit(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public boolean updateDimensionHierarchies(List<DimensionHierarchy> updates) {
		Connection con = null;
		try {
			con = dslContext.configuration().connectionProvider().acquire();
			con.setAutoCommit(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (DimensionHierarchy hierarchy : updates) {
			List<Dimension> levels = hierarchy.getLevels();
			List<Dimension> changedLevels = hierarchy.getLevelsProperty().get();
			if (levels.size() <= changedLevels.size()) {
				int i = 0;
				for (; i < levels.size() - 1; i++) {
					UpdateConditionStep<DimensionHierarchiesRecord> statement = dslContext
																				.update(DIMENSION_HIERARCHIES)
																				.set(DIMENSION_HIERARCHIES.PARENT_ID, changedLevels.get(i).getId())
																				.set(DIMENSION_HIERARCHIES.CHILD_ID, changedLevels.get(i + 1).getId())
																				.where(DIMENSION_HIERARCHIES.PARENT_ID.equal(levels.get(i).getId())
																					.and(DIMENSION_HIERARCHIES.CHILD_ID.equal(levels.get(i + 1).getId())));
					
					statement.execute();
					
					Main.getLogger().info("Dimension Hierarchy updated.");
					Main.getLogger().info("Executed SQL: " + statement.getSQL(ParamType.INLINED));
				}
				for (; i < changedLevels.size() - 1; i++) {
					InsertValuesStep2<DimensionHierarchiesRecord, Long, Long> statement = dslContext
																							.insertInto(DIMENSION_HIERARCHIES, DIMENSION_HIERARCHIES.PARENT_ID, DIMENSION_HIERARCHIES.CHILD_ID)
																							.values(changedLevels.get(i).getId(), changedLevels.get(i + 1).getId());
					
					statement.execute();
					
					Main.getLogger().info("Dimension Hierarchy created.");
					Main.getLogger().info("Executed SQL: " + statement.getSQL(ParamType.INLINED));
				}
			} else {
				int i = 0;
				for (; i < changedLevels.size() - 1; i++) {
					UpdateConditionStep<DimensionHierarchiesRecord> statement = dslContext
																				.update(DIMENSION_HIERARCHIES)
																				.set(DIMENSION_HIERARCHIES.PARENT_ID, changedLevels.get(i).getId())
																				.set(DIMENSION_HIERARCHIES.CHILD_ID, changedLevels.get(i + 1).getId())
																				.where(DIMENSION_HIERARCHIES.PARENT_ID.equal(levels.get(i).getId())
																					.and(DIMENSION_HIERARCHIES.CHILD_ID.equal(levels.get(i + 1).getId())));
					
					statement.execute();
					
					Main.getLogger().info("Dimension Hierarchy updated.");
					Main.getLogger().info("Executed SQL: " + statement.getSQL(ParamType.INLINED));
				}
				for (; i < levels.size() - 1; i++) {
					DeleteConditionStep<DimensionHierarchiesRecord> statement = dslContext
																				.delete(DIMENSION_HIERARCHIES)
																				.where(DIMENSION_HIERARCHIES.PARENT_ID.equal(levels.get(i).getId())
																					.and(DIMENSION_HIERARCHIES.CHILD_ID.equal(levels.get(i + 1).getId())));
					
					statement.execute();
					
					Main.getLogger().info("Dimension Hierarchy deleted.");
					Main.getLogger().info("Executed SQL: " + statement.getSQL(ParamType.INLINED));
				}
			}
		}
		
		try {
			con.setAutoCommit(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public boolean deleteDimensionHierarchies(List<DimensionHierarchy> deletions) {
		Connection con = null;
		try {
			con = dslContext.configuration().connectionProvider().acquire();
			con.setAutoCommit(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (DimensionHierarchy hierarchy : deletions) {
			List<Dimension> levels = hierarchy.getLevels();
			for (int i = 0; i < levels.size() - 1; i++) {
				DeleteConditionStep<DimensionHierarchiesRecord> statement = dslContext
																			.delete(DIMENSION_HIERARCHIES)
																			.where(DIMENSION_HIERARCHIES.PARENT_ID.equal(levels.get(i).getId())
																				.and(DIMENSION_HIERARCHIES.CHILD_ID.equal(levels.get(i + 1).getId())));
				
				statement.execute();
				
				Main.getLogger().info("Dimension Hierarchy deleted.");
				Main.getLogger().info("Executed SQL: " + statement.getSQL(ParamType.INLINED));
			}
		}
		
		try {
			con.setAutoCommit(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	
	@Override
	public boolean createReferenceObjectHierarchies(List<ReferenceObjectHierarchy> creations) {
		Connection con = null;
		try {
			con = dslContext.configuration().connectionProvider().acquire();
			con.setAutoCommit(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (ReferenceObjectHierarchy hierarchy : creations) {
			List<ReferenceObject> levels = hierarchy.getLevelsProperty().get();
			for (int i = 0; i < levels.size() - 1; i++) {
				InsertValuesStep2<ReferenceObjectHierarchiesRecord, Long, Long> statement = dslContext
																							.insertInto(REFERENCE_OBJECT_HIERARCHIES, REFERENCE_OBJECT_HIERARCHIES.PARENT_ID, REFERENCE_OBJECT_HIERARCHIES.CHILD_ID)
																							.values(levels.get(i).getId(), levels.get(i + 1).getId());
				
				statement.execute();
				
				Main.getLogger().info("Reference Object Hierarchy created.");
				Main.getLogger().info("Executed SQL: " + statement.getSQL(ParamType.INLINED));
			}
		}
		
		try {
			con.setAutoCommit(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public boolean updateReferenceObjectHierarchies(List<ReferenceObjectHierarchy> updates) {
		Connection con = null;
		try {
			con = dslContext.configuration().connectionProvider().acquire();
			con.setAutoCommit(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (ReferenceObjectHierarchy hierarchy : updates) {
			List<ReferenceObject> levels = hierarchy.getLevels();
			List<ReferenceObject> changedLevels = hierarchy.getLevelsProperty().get();
			if (levels.size() <= changedLevels.size()) {
				int i = 0;
				for (; i < levels.size() - 1; i++) {
					UpdateConditionStep<ReferenceObjectHierarchiesRecord> statement = dslContext
																						.update(REFERENCE_OBJECT_HIERARCHIES)
																						.set(REFERENCE_OBJECT_HIERARCHIES.PARENT_ID, changedLevels.get(i).getId())
																						.set(REFERENCE_OBJECT_HIERARCHIES.CHILD_ID, changedLevels.get(i + 1).getId())
																						.where(REFERENCE_OBJECT_HIERARCHIES.PARENT_ID.equal(levels.get(i).getId())
																							.and(REFERENCE_OBJECT_HIERARCHIES.CHILD_ID.equal(levels.get(i + 1).getId())));
					
					statement.execute();
					
					Main.getLogger().info("Reference Object Hierarchy updated.");
					Main.getLogger().info("Executed SQL: " + statement.getSQL(ParamType.INLINED));
				}
				for (; i < changedLevels.size() - 1; i++) {
					InsertValuesStep2<ReferenceObjectHierarchiesRecord, Long, Long> statement = dslContext
																								.insertInto(REFERENCE_OBJECT_HIERARCHIES, REFERENCE_OBJECT_HIERARCHIES.PARENT_ID, REFERENCE_OBJECT_HIERARCHIES.CHILD_ID)
																								.values(changedLevels.get(i).getId(), changedLevels.get(i + 1).getId());
					
					statement.execute();
					
					Main.getLogger().info("Reference Object Hierarchy created.");
					Main.getLogger().info("Executed SQL: " + statement.getSQL(ParamType.INLINED));
				}
			} else {
				int i = 0;
				for (; i < changedLevels.size() - 1; i++) {
					UpdateConditionStep<ReferenceObjectHierarchiesRecord> statement = dslContext
																						.update(REFERENCE_OBJECT_HIERARCHIES)
																						.set(REFERENCE_OBJECT_HIERARCHIES.PARENT_ID, changedLevels.get(i).getId())
																						.set(REFERENCE_OBJECT_HIERARCHIES.CHILD_ID, changedLevels.get(i + 1).getId())
																						.where(REFERENCE_OBJECT_HIERARCHIES.PARENT_ID.equal(levels.get(i).getId())
																							.and(REFERENCE_OBJECT_HIERARCHIES.CHILD_ID.equal(levels.get(i + 1).getId())));
					
					statement.execute();
					
					Main.getLogger().info("Reference Object Hierarchy updated.");
					Main.getLogger().info("Executed SQL: " + statement.getSQL(ParamType.INLINED));
				}
				for (; i < levels.size() - 1; i++) {
					DeleteConditionStep<ReferenceObjectHierarchiesRecord> statement = dslContext
																						.delete(REFERENCE_OBJECT_HIERARCHIES)
																						.where(REFERENCE_OBJECT_HIERARCHIES.PARENT_ID.equal(levels.get(i).getId())
																							.and(REFERENCE_OBJECT_HIERARCHIES.CHILD_ID.equal(levels.get(i + 1).getId())));
					
					statement.execute();
					
					Main.getLogger().info("Reference Object Hierarchy deleted.");
					Main.getLogger().info("Executed SQL: " + statement.getSQL(ParamType.INLINED));
				}
			}
		}
		
		try {
			con.setAutoCommit(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public boolean deleteReferenceObjectHierarchies(List<ReferenceObjectHierarchy> deletions) {
		Connection con = null;
		try {
			con = dslContext.configuration().connectionProvider().acquire();
			con.setAutoCommit(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (ReferenceObjectHierarchy hierarchy : deletions) {
			List<ReferenceObject> levels = hierarchy.getLevels();
			for (int i = 0; i < levels.size() - 1; i++) {
				DeleteConditionStep<ReferenceObjectHierarchiesRecord> statement = dslContext
																					.delete(REFERENCE_OBJECT_HIERARCHIES)
																					.where(REFERENCE_OBJECT_HIERARCHIES.PARENT_ID.equal(levels.get(i).getId())
																						.and(REFERENCE_OBJECT_HIERARCHIES.CHILD_ID.equal(levels.get(i + 1).getId())));
				
				statement.execute();
				
				Main.getLogger().info("Reference Object Hierarchy deleted.");
				Main.getLogger().info("Executed SQL: " + statement.getSQL(ParamType.INLINED));
			}
		}
		
		try {
			con.setAutoCommit(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	
	@Override
	public boolean createDimensionCombinations(List<DimensionCombination> creations) {
		Connection con = null;
		try {
			con = dslContext.configuration().connectionProvider().acquire();
			con.setAutoCommit(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (DimensionCombination combination : creations) {
			InsertValuesStep1<DimensionsRecord, String> statement = dslContext
																	.insertInto(DIMENSIONS, DIMENSIONS.NAME)
																	.values(combination.getNameProperty().get());
			
			statement.execute();
			
			Main.getLogger().info("Dimension created.");
			Main.getLogger().info("Executed SQL: " + statement.getSQL(ParamType.INLINED));
			
			try {
				con.commit();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			long lastID = dslContext.lastID().longValue();
			
			try {
				con.commit();
			} catch (Exception e) {
				e.printStackTrace();
			}
					
			List<Dimension> components = combination.getComponentsProperty().get();
			for (int i = 0; i < components.size(); i++) {
				InsertValuesStep2<DimensionCombinationsRecord, Long, Long> statement1 = dslContext
																						.insertInto(DIMENSION_COMBINATIONS, DIMENSION_COMBINATIONS.AGGREGATE_ID, DIMENSION_COMBINATIONS.COMPONENT_ID)
																						.values(lastID, components.get(i).getId());
				
				statement1.execute();
				
				Main.getLogger().info("Dimension Combination created.");
				Main.getLogger().info(statement1.getSQL(ParamType.INLINED));
			}
		}
		
		try {
			con.setAutoCommit(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public boolean updateDimensionCombinations(List<DimensionCombination> updates) {
		Connection con = null;
		try {
			con = dslContext.configuration().connectionProvider().acquire();
			con.setAutoCommit(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (DimensionCombination combination : updates) {
			UpdateConditionStep<DimensionsRecord> statement = dslContext
																.update(DIMENSIONS)
																.set(DIMENSIONS.NAME, combination.getNameProperty().get())
																.where(DIMENSIONS.DIMENSION_ID.equal(combination.getCombination().getId()));
			
			statement.execute();
			
			Main.getLogger().info("Dimension updated.");
			Main.getLogger().info("Executed SQL: " + statement.getSQL(ParamType.INLINED));
			
			List<Dimension> components = combination.getComponents();
			List<Dimension> changedComponents = combination.getComponentsProperty().get();
			if (components.size() <= changedComponents.size()) {
				int i = 0;
				for (; i < components.size(); i++) {
					UpdateConditionStep<DimensionCombinationsRecord> statement1 = dslContext
																					.update(DIMENSION_COMBINATIONS)
																					.set(DIMENSION_COMBINATIONS.COMPONENT_ID, changedComponents.get(i).getId())
																					.where(DIMENSION_COMBINATIONS.AGGREGATE_ID.equal(combination.getCombination().getId())
																						.and(DIMENSION_COMBINATIONS.COMPONENT_ID.equal(components.get(i).getId())));
					
					statement1.execute();
					
					Main.getLogger().info("Dimension Combination updated.");
					Main.getLogger().info(statement1.getSQL(ParamType.INLINED));
				}
				for (; i < changedComponents.size(); i++) {
					InsertValuesStep2<DimensionCombinationsRecord, Long, Long> statement1 = dslContext
																							.insertInto(DIMENSION_COMBINATIONS, DIMENSION_COMBINATIONS.AGGREGATE_ID, DIMENSION_COMBINATIONS.COMPONENT_ID)
																							.values(combination.getCombination().getId(), changedComponents.get(i).getId());
					
					statement1.execute();
					
					Main.getLogger().info("Dimension Combination created.");
					Main.getLogger().info(statement1.getSQL(ParamType.INLINED));
				}
			} else {
				int i = 0;
				for (; i < changedComponents.size(); i++) {
					UpdateConditionStep<DimensionCombinationsRecord> statement1 = dslContext
																					.update(DIMENSION_COMBINATIONS)
																					.set(DIMENSION_COMBINATIONS.COMPONENT_ID, changedComponents.get(i).getId())
																					.where(DIMENSION_COMBINATIONS.AGGREGATE_ID.equal(combination.getCombination().getId())
																						.and(DIMENSION_COMBINATIONS.COMPONENT_ID.equal(components.get(i).getId())));
					
					statement1.execute();
					
					Main.getLogger().info("Dimension Combination updated.");
					Main.getLogger().info(statement1.getSQL(ParamType.INLINED));
				}
				for (; i < components.size(); i++) {
					DeleteConditionStep<DimensionCombinationsRecord> statement1 = dslContext
																					.delete(DIMENSION_COMBINATIONS)
																					.where(DIMENSION_COMBINATIONS.AGGREGATE_ID.equal(combination.getCombination().getId())
																						.and(DIMENSION_COMBINATIONS.COMPONENT_ID.equal(components.get(i).getId())));
					
					try {
						statement1.execute();
					} catch (DataAccessException e) {
						Main.getLogger().info("Could not delete Dimension Combination. Dimension Combination possibly still referenced elsewhere.");
						Main.getLogger().info("Executed SQL: " + statement.getSQL(ParamType.INLINED));
						return false;
					}
					
					Main.getLogger().info("Dimension Combination deleted.");
					Main.getLogger().info(statement1.getSQL(ParamType.INLINED));
				}
			}
		}
		
		try {
			con.setAutoCommit(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public boolean deleteDimensionCombinations(List<DimensionCombination> deletions) {
		Connection con = null;
		try {
			con = dslContext.configuration().connectionProvider().acquire();
			con.setAutoCommit(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (DimensionCombination combination : deletions) {
			DeleteConditionStep<DimensionsRecord> statement = dslContext
																.delete(DIMENSIONS)
																.where(DIMENSIONS.DIMENSION_ID.equal(combination.getCombination().getId()));
			
			statement.execute();
			
			Main.getLogger().info("Dimension deleted.");
			Main.getLogger().info("Executed SQL: " + statement.getSQL(ParamType.INLINED));
			
			DeleteConditionStep<DimensionCombinationsRecord> statement1 = dslContext
																			.delete(DIMENSION_COMBINATIONS)
																			.where(DIMENSION_COMBINATIONS.AGGREGATE_ID.equal(combination.getCombination().getId()));
			
			try {
				statement1.execute();
			} catch (DataAccessException dae) {
				Main.getLogger().info("Could not delete Dimension Combination. Dimension Combination possibly still referenced elsewhere.");
				Main.getLogger().info("Executed SQL: " + statement.getSQL(ParamType.INLINED));
				try {
					con.rollback();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return false;
			}
			
			Main.getLogger().info("Dimension Combination deleted.");
			Main.getLogger().info("Executed SQL: " + statement1.getSQL(ParamType.INLINED));
		}
		
		try {
			con.setAutoCommit(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	
	@Override
	public boolean createReferenceObjectCombinations(List<ReferenceObjectCombination> creations) {
		Connection con = null;
		try {
			con = dslContext.configuration().connectionProvider().acquire();
			con.setAutoCommit(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (ReferenceObjectCombination combination : creations) {
			InsertValuesStep2<ReferenceObjectsRecord, Long, String> statement = dslContext
																				.insertInto(REFERENCE_OBJECTS, REFERENCE_OBJECTS.DIMENSION_ID, REFERENCE_OBJECTS.NAME)
																				.values(combination.getCombination().getDimensionProperty().get().getId(), combination.getNameProperty().get());
			
			statement.execute();
			
			Main.getLogger().info("Reference Object created.");
			Main.getLogger().info("Executed SQL: " + statement.getSQL(ParamType.INLINED));
			
			try {
				con.commit();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			long lastID = dslContext.lastID().longValue();
			
			try {
				con.commit();
			} catch (Exception e) {
				e.printStackTrace();
			}
					
			List<ReferenceObject> components = combination.getComponentsProperty().get();
			for (int i = 0; i < components.size(); i++) {
				InsertValuesStep2<ReferenceObjectCombinationsRecord, Long, Long> statement1 = dslContext
																								.insertInto(REFERENCE_OBJECT_COMBINATIONS, REFERENCE_OBJECT_COMBINATIONS.AGGREGATE_ID, REFERENCE_OBJECT_COMBINATIONS.COMPONENT_ID)
																								.values(lastID, components.get(i).getId());
				
				statement1.execute();
				
				Main.getLogger().info("Reference Object Combination created.");
				Main.getLogger().info(statement1.getSQL(ParamType.INLINED));
			}
		}
		
		try {
			con.setAutoCommit(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public boolean updateReferenceObjectCombinations(List<ReferenceObjectCombination> updates) {
		Connection con = null;
		try {
			con = dslContext.configuration().connectionProvider().acquire();
			con.setAutoCommit(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (ReferenceObjectCombination combination : updates) {
			UpdateConditionStep<ReferenceObjectsRecord> statement = dslContext
																	.update(REFERENCE_OBJECTS)
																	.set(REFERENCE_OBJECTS.DIMENSION_ID, combination.getCombination().getDimensionProperty().get().getId())
																	.set(REFERENCE_OBJECTS.NAME, combination.getNameProperty().get())
																	.where(REFERENCE_OBJECTS.REFERENCE_OBJECT_ID.equal(combination.getCombination().getId()));
			
			statement.execute();
			
			Main.getLogger().info("Reference Object updated.");
			Main.getLogger().info("Executed SQL: " + statement.getSQL(ParamType.INLINED));
			
			List<ReferenceObject> components = combination.getComponents();
			List<ReferenceObject> changedComponents = combination.getComponentsProperty().get();
			if (components.size() <= changedComponents.size()) {
				int i = 0;
				for (; i < components.size(); i++) {
					UpdateConditionStep<ReferenceObjectCombinationsRecord> statement1 = dslContext
																						.update(REFERENCE_OBJECT_COMBINATIONS)
																						.set(REFERENCE_OBJECT_COMBINATIONS.COMPONENT_ID, changedComponents.get(i).getId())
																						.where(REFERENCE_OBJECT_COMBINATIONS.AGGREGATE_ID.equal(combination.getCombination().getId())
																							.and(REFERENCE_OBJECT_COMBINATIONS.COMPONENT_ID.equal(components.get(i).getId())));
					
					statement1.execute();
					
					Main.getLogger().info("Reference Object Combination updated.");
					Main.getLogger().info(statement1.getSQL(ParamType.INLINED));
				}
				for (; i < changedComponents.size(); i++) {
					InsertValuesStep2<ReferenceObjectCombinationsRecord, Long, Long> statement1 = dslContext
																									.insertInto(REFERENCE_OBJECT_COMBINATIONS, REFERENCE_OBJECT_COMBINATIONS.AGGREGATE_ID, REFERENCE_OBJECT_COMBINATIONS.COMPONENT_ID)
																									.values(combination.getCombination().getId(), changedComponents.get(i).getId());
					
					statement1.execute();
					
					Main.getLogger().info("Reference Object Combination created.");
					Main.getLogger().info(statement1.getSQL(ParamType.INLINED));
				}
			} else {
				int i = 0;
				for (; i < changedComponents.size(); i++) {
					UpdateConditionStep<ReferenceObjectCombinationsRecord> statement1 = dslContext
																						.update(REFERENCE_OBJECT_COMBINATIONS)
																						.set(REFERENCE_OBJECT_COMBINATIONS.COMPONENT_ID, changedComponents.get(i).getId())
																						.where(REFERENCE_OBJECT_COMBINATIONS.AGGREGATE_ID.equal(combination.getCombination().getId())
																							.and(REFERENCE_OBJECT_COMBINATIONS.COMPONENT_ID.equal(components.get(i).getId())));
					
					statement1.execute();
					
					Main.getLogger().info("Reference Object Combination updated.");
					Main.getLogger().info(statement1.getSQL(ParamType.INLINED));
				}
				for (; i < components.size(); i++) {
					DeleteConditionStep<ReferenceObjectCombinationsRecord> statement1 = dslContext
																						.delete(REFERENCE_OBJECT_COMBINATIONS)
																						.where(REFERENCE_OBJECT_COMBINATIONS.AGGREGATE_ID.equal(combination.getCombination().getId())
																							.and(REFERENCE_OBJECT_COMBINATIONS.COMPONENT_ID.equal(components.get(i).getId())));
					
					try {
						statement1.execute();
					} catch (DataAccessException e) {
						Main.getLogger().info("Could not delete Reference Object Combination. Reference Object Combination possibly still referenced elsewhere.");
						Main.getLogger().info("Executed SQL: " + statement.getSQL(ParamType.INLINED));
						return false;
					}
					
					Main.getLogger().info("Reference Object Combination deleted.");
					Main.getLogger().info(statement1.getSQL(ParamType.INLINED));
				}
			}
		}
		
		try {
			con.setAutoCommit(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public boolean deleteReferenceObjectCombinations(List<ReferenceObjectCombination> deletions) {
		Connection con = null;
		try {
			con = dslContext.configuration().connectionProvider().acquire();
			con.setAutoCommit(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (ReferenceObjectCombination combination : deletions) {
			DeleteConditionStep<ReferenceObjectsRecord> statement = dslContext
																		.delete(REFERENCE_OBJECTS)
																		.where(REFERENCE_OBJECTS.REFERENCE_OBJECT_ID.equal(combination.getCombination().getId()));
			
			statement.execute();
			
			Main.getLogger().info("Reference Object deleted.");
			Main.getLogger().info("Executed SQL: " + statement.getSQL(ParamType.INLINED));
			
			DeleteConditionStep<ReferenceObjectCombinationsRecord> statement1 = dslContext
																				.delete(REFERENCE_OBJECT_COMBINATIONS)
																				.where(REFERENCE_OBJECT_COMBINATIONS.AGGREGATE_ID.equal(combination.getCombination().getId()));
			
			try {
				statement1.execute();
			} catch (DataAccessException dae) {
				Main.getLogger().info("Could not delete Reference Object Combination. Reference Object Combination possibly still referenced elsewhere.");
				Main.getLogger().info("Executed SQL: " + statement.getSQL(ParamType.INLINED));
				try {
					con.rollback();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return false;
			}
			
			Main.getLogger().info("Reference Object Copmbination deleted.");
			Main.getLogger().info("Executed SQL: " + statement1.getSQL(ParamType.INLINED));
		}
		
		try {
			con.setAutoCommit(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	
	@Override
	public boolean createRatios(List<Ratio> creations) {
		Connection con = null;
		try {
			con = dslContext.configuration().connectionProvider().acquire();
			con.setAutoCommit(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (Ratio ratio : creations) {
			Long catId = ratio.getCategoryProperty().get().getId() == 0 ? null : ratio.getCategoryProperty().get().getId();
			InsertValuesStep2<RatiosRecord, String, Long> statement = dslContext
																		.insertInto(RATIOS, RATIOS.NAME, RATIOS.CATEGORY_ID)
																		.values(ratio.getNameProperty().get(), catId);
			
			statement.execute();
			
			Main.getLogger().info("Ratio created.");
			Main.getLogger().info("Executed SQL: " + statement.getSQL(ParamType.INLINED));
		}
		
		try {
			con.setAutoCommit(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public boolean updateRatios(List<Ratio> updates) {
		Connection con = null;
		try {
			con = dslContext.configuration().connectionProvider().acquire();
			con.setAutoCommit(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (Ratio ratio : updates) {
			Long catId = ratio.getCategoryProperty().get().getId() == 0 ? null : ratio.getCategoryProperty().get().getId();
			UpdateConditionStep<RatiosRecord> statement = dslContext
															.update(RATIOS)
															.set(RATIOS.NAME, ratio.getNameProperty().get())
															.set(RATIOS.CATEGORY_ID, catId)
															.where(RATIOS.RATIO_ID.equal(ratio.getId()));
			
			statement.execute();
			
			Main.getLogger().info("Ratio updated.");
			Main.getLogger().info("Executed SQL: " + statement.getSQL(ParamType.INLINED));
		}
		
		try {
			con.setAutoCommit(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public boolean deleteRatios(List<Ratio> deletions) {
		Connection con = null;
		try {
			con = dslContext.configuration().connectionProvider().acquire();
			con.setAutoCommit(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (Ratio ratio : deletions) {
			DeleteConditionStep<RatiosRecord> statement = dslContext
															.delete(RATIOS)
															.where(RATIOS.RATIO_ID.equal(ratio.getId()));
			
			try {
				statement.execute();
			} catch (DataAccessException e) {
				Main.getLogger().info("Could not delete Ratio. Ratio possibly still referenced elsewhere.");
				Main.getLogger().info("Executed SQL: " + statement.getSQL(ParamType.INLINED));
				return false;
			}
			
			Main.getLogger().info("Ratio deleted.");
			Main.getLogger().info("Executed SQL: " + statement.getSQL(ParamType.INLINED));
		}
		
		try {
			con.setAutoCommit(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	
	@Override
	public boolean createRatioRelations(List<RatioRelation> creations) {
		Connection con = null;
		try {
			con = dslContext.configuration().connectionProvider().acquire();
			con.setAutoCommit(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (RatioRelation relation : creations) {
			List<Ratio> levels = relation.getLevelsProperty().get();
			for (int i = 0; i < levels.size() - 1; i++) {
				InsertValuesStep2<RatioRelationsRecord, Long, Long> statement = dslContext
																				.insertInto(RATIO_RELATIONS, RATIO_RELATIONS.DEPENDENT_ID, RATIO_RELATIONS.DEPENDENCY_ID)
																				.values(levels.get(i).getId(), levels.get(i + 1).getId());
				
				statement.execute();
				
				Main.getLogger().info("Ratio Relation created.");
				Main.getLogger().info("Executed SQL: " + statement.getSQL(ParamType.INLINED));
			}
		}
		
		try {
			con.setAutoCommit(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public boolean updateRatioRelations(List<RatioRelation> updates) {
		Connection con = null;
		try {
			con = dslContext.configuration().connectionProvider().acquire();
			con.setAutoCommit(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (RatioRelation relation : updates) {
			List<Ratio> levels = relation.getLevels();
			List<Ratio> changedLevels = relation.getLevelsProperty().get();
			if (levels.size() <= changedLevels.size()) {
				int i = 0;
				for (; i < levels.size() - 1; i++) {
					UpdateConditionStep<RatioRelationsRecord> statement = dslContext
																			.update(RATIO_RELATIONS)
																			.set(RATIO_RELATIONS.DEPENDENT_ID, changedLevels.get(i).getId())
																			.set(RATIO_RELATIONS.DEPENDENCY_ID, changedLevels.get(i + 1).getId())
																			.where(RATIO_RELATIONS.DEPENDENT_ID.equal(levels.get(i).getId())
																				.and(RATIO_RELATIONS.DEPENDENCY_ID.equal(levels.get(i + 1).getId())));
					
					statement.execute();
					
					Main.getLogger().info("Ratio Relation updated.");
					Main.getLogger().info("Executed SQL: " + statement.getSQL(ParamType.INLINED));
				}
				for (; i < changedLevels.size() - 1; i++) {
					InsertValuesStep2<RatioRelationsRecord, Long, Long> statement = dslContext
																					.insertInto(RATIO_RELATIONS, RATIO_RELATIONS.DEPENDENT_ID, RATIO_RELATIONS.DEPENDENCY_ID)
																					.values(changedLevels.get(i).getId(), changedLevels.get(i + 1).getId());
					
					statement.execute();
					
					Main.getLogger().info("Ratio Relation created.");
					Main.getLogger().info("Executed SQL: " + statement.getSQL(ParamType.INLINED));
				}
			} else {
				int i = 0;
				for (; i < changedLevels.size() - 1; i++) {
					UpdateConditionStep<RatioRelationsRecord> statement = dslContext
																				.update(RATIO_RELATIONS)
																				.set(RATIO_RELATIONS.DEPENDENT_ID, changedLevels.get(i).getId())
																				.set(RATIO_RELATIONS.DEPENDENCY_ID, changedLevels.get(i + 1).getId())
																				.where(RATIO_RELATIONS.DEPENDENT_ID.equal(levels.get(i).getId())
																					.and(RATIO_RELATIONS.DEPENDENCY_ID.equal(levels.get(i + 1).getId())));
					
					statement.execute();
					
					Main.getLogger().info("Ratio Relation updated.");
					Main.getLogger().info("Executed SQL: " + statement.getSQL(ParamType.INLINED));
				}
				for (; i < levels.size() - 1; i++) {
					DeleteConditionStep<RatioRelationsRecord> statement = dslContext
																				.delete(RATIO_RELATIONS)
																				.where(RATIO_RELATIONS.DEPENDENT_ID.equal(levels.get(i).getId())
																					.and(RATIO_RELATIONS.DEPENDENCY_ID.equal(levels.get(i + 1).getId())));
					
					statement.execute();
					
					Main.getLogger().info("Ratio Relation deleted.");
					Main.getLogger().info("Executed SQL: " + statement.getSQL(ParamType.INLINED));
				}
			}
		}
		
		try {
			con.setAutoCommit(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public boolean deleteRatioRelations(List<RatioRelation> deletions) {
		Connection con = null;
		try {
			con = dslContext.configuration().connectionProvider().acquire();
			con.setAutoCommit(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (RatioRelation relation : deletions) {
			List<Ratio> levels = relation.getLevels();
			for (int i = 0; i < levels.size() - 1; i++) {
				DeleteConditionStep<RatioRelationsRecord> statement = dslContext
																			.delete(RATIO_RELATIONS)
																			.where(RATIO_RELATIONS.DEPENDENT_ID.equal(levels.get(i).getId())
																				.and(RATIO_RELATIONS.DEPENDENCY_ID.equal(levels.get(i + 1).getId())));
				
				statement.execute();
				
				Main.getLogger().info("Ratio Relation deleted.");
				Main.getLogger().info("Executed SQL: " + statement.getSQL(ParamType.INLINED));
			}
		}
		
		try {
			con.setAutoCommit(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	

	@Override
	public boolean createFacts(List<Fact> creations) {
		Connection con = null;
		try {
			con = dslContext.configuration().connectionProvider().acquire();
			con.setAutoCommit(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (Fact fact : creations) {
			Long unitId = fact.getUnitProperty().get().getId() == 0 ? null : fact.getUnitProperty().get().getId();
			InsertValuesStep4<FactsRecord, Long, Long, Double, Long> statement = dslContext
																					.insertInto(FACTS, FACTS.RATIO_ID, FACTS.REFERENCE_OBJECT_ID, FACTS.VALUE, FACTS.UNIT_ID)
																					.values(fact.getRatioProperty().get().getId(),
																							fact.getReferenceObjectProperty().get().getId(),
																							fact.getValueProperty().get(),
																							unitId);
			
			statement.execute();
			
			Main.getLogger().info("Fact created.");
			Main.getLogger().info("Executed SQL: " + statement.getSQL(ParamType.INLINED));
		}
		
		try {
			con.setAutoCommit(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public boolean updateFacts(List<Fact> updates) {
		Connection con = null;
		try {
			con = dslContext.configuration().connectionProvider().acquire();
			con.setAutoCommit(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (Fact fact : updates) {
			Long unitId = fact.getUnitProperty().get().getId() == 0 ? null : fact.getUnitProperty().get().getId();
			UpdateConditionStep<FactsRecord> statement = dslContext
															.update(FACTS)
															.set(FACTS.RATIO_ID, fact.getRatioProperty().get().getId())
															.set(FACTS.REFERENCE_OBJECT_ID, fact.getReferenceObjectProperty().get().getId())
															.set(FACTS.VALUE, fact.getValueProperty().get())
															.set(FACTS.UNIT_ID, unitId)
															.where(FACTS.RATIO_ID.equal(fact.getRatioProperty().get().getId())
																.and(FACTS.REFERENCE_OBJECT_ID.equal(fact.getReferenceObjectProperty().get().getId())));
			
			statement.execute();
			
			Main.getLogger().info("Fact updated.");
			Main.getLogger().info("Executed SQL: " + statement.getSQL(ParamType.INLINED));
		}
		
		try {
			con.setAutoCommit(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public boolean deleteFacts(List<Fact> deletions) {
		Connection con = null;
		try {
			con = dslContext.configuration().connectionProvider().acquire();
			con.setAutoCommit(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (Fact fact : deletions) {
			DeleteConditionStep<FactsRecord> statement = dslContext
															.delete(FACTS)
															.where(FACTS.RATIO_ID.equal(fact.getRatioProperty().get().getId())
																.and(FACTS.REFERENCE_OBJECT_ID.equal(fact.getReferenceObjectProperty().get().getId())));
			
			statement.execute();
			
			Main.getLogger().info("Fact deleted.");
			Main.getLogger().info("Executed SQL: " + statement.getSQL(ParamType.INLINED));
		}
		
		try {
			con.setAutoCommit(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	
	@Override
	public boolean createDimensionCategories(List<DimensionCategory> creations) {
		Connection con = null;
		try {
			con = dslContext.configuration().connectionProvider().acquire();
			con.setAutoCommit(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (DimensionCategory cat : creations) {
			InsertValuesStep1<DimensionCategoriesRecord, String> statement = dslContext
																				.insertInto(DIMENSION_CATEGORIES, DIMENSION_CATEGORIES.NAME)
																				.values(cat.getNameProperty().get());
			
			statement.execute();
			
			Main.getLogger().info("Dimension Category created.");
			Main.getLogger().info("Executed SQL: " + statement.getSQL(ParamType.INLINED));
		}
		
		try {
			con.setAutoCommit(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public boolean updateDimensionCategories(List<DimensionCategory> updates) {
		Connection con = null;
		try {
			con = dslContext.configuration().connectionProvider().acquire();
			con.setAutoCommit(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (DimensionCategory cat : updates) {
			UpdateConditionStep<DimensionCategoriesRecord> statement = dslContext
																		.update(DIMENSION_CATEGORIES)
																		.set(DIMENSION_CATEGORIES.NAME, cat.getNameProperty().get())
																		.where(DIMENSION_CATEGORIES.CATEGORY_ID.equal(cat.getId()));

			statement.execute();
			
			Main.getLogger().info("Dimension Category updated.");
			Main.getLogger().info("Executed SQL: " + statement.getSQL(ParamType.INLINED));
		}
		
		try {
			con.setAutoCommit(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public boolean deleteDimensionCategories(List<DimensionCategory> deletions) {
		Connection con = null;
		try {
			con = dslContext.configuration().connectionProvider().acquire();
			con.setAutoCommit(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (DimensionCategory cat : deletions) {
			DeleteConditionStep<DimensionCategoriesRecord> statement = dslContext
																		.delete(DIMENSION_CATEGORIES)
																		.where(DIMENSION_CATEGORIES.CATEGORY_ID.equal(cat.getId()));
			
			try {
				statement.execute();
			} catch (DataAccessException e) {
				Main.getLogger().info("Could not delete Dimension Category. Dimension Category possibly still referenced elsewhere.");
				Main.getLogger().info("Executed SQL: " + statement.getSQL(ParamType.INLINED));
				return false;
			}
			
			Main.getLogger().info("Dimension Category deleted.");
			Main.getLogger().info("Executed SQL: " + statement.getSQL(ParamType.INLINED));
		}
		
		try {
			con.setAutoCommit(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	
	@Override
	public boolean createRatioCategories(List<RatioCategory> creations) {
		Connection con = null;
		try {
			con = dslContext.configuration().connectionProvider().acquire();
			con.setAutoCommit(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (RatioCategory cat : creations) {
			InsertValuesStep1<RatioCategoriesRecord, String> statement = dslContext
																			.insertInto(RATIO_CATEGORIES, RATIO_CATEGORIES.NAME)
																			.values(cat.getNameProperty().get());
			
			statement.execute();
			
			Main.getLogger().info("Ratio Category created.");
			Main.getLogger().info("Executed SQL: " + statement.getSQL(ParamType.INLINED));
		}
		
		try {
			con.setAutoCommit(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public boolean updateRatioCategories(List<RatioCategory> updates) {
		Connection con = null;
		try {
			con = dslContext.configuration().connectionProvider().acquire();
			con.setAutoCommit(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (RatioCategory cat : updates) {
			UpdateConditionStep<RatioCategoriesRecord> statement = dslContext
																	.update(RATIO_CATEGORIES)
																	.set(RATIO_CATEGORIES.NAME, cat.getNameProperty().get())
																	.where(RATIO_CATEGORIES.CATEGORY_ID.equal(cat.getId()));
			
			statement.execute();
			
			Main.getLogger().info("Ratio Category updated.");
			Main.getLogger().info("Executed SQL: " + statement.getSQL(ParamType.INLINED));

		}
		
		try {
			con.setAutoCommit(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public boolean deleteRatioCategories(List<RatioCategory> deletions) {
		Connection con = null;
		try {
			con = dslContext.configuration().connectionProvider().acquire();
			con.setAutoCommit(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (RatioCategory cat : deletions) {
			DeleteConditionStep<RatioCategoriesRecord> statement = dslContext
																	.delete(RATIO_CATEGORIES)
																	.where(RATIO_CATEGORIES.CATEGORY_ID.equal(cat.getId()));
			
			try {
				statement.execute();
			} catch (DataAccessException e) {
				Main.getLogger().info("Could not delete Ratio Category. Ratio Category possibly still referenced elsewhere.");
				Main.getLogger().info("Executed SQL: " + statement.getSQL(ParamType.INLINED));
				return false;
			}
			
			Main.getLogger().info("Ratio Category deleted.");
			Main.getLogger().info("Executed SQL: " + statement.getSQL(ParamType.INLINED));
		}
		
		try {
			con.setAutoCommit(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	
	@Override
	public boolean createUnits(List<Unit> creations) {
		Connection con = null;
		try {
			con = dslContext.configuration().connectionProvider().acquire();
			con.setAutoCommit(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (Unit unit : creations) {
			InsertValuesStep2<FactUnitsRecord, String, String> statement = dslContext
																			.insertInto(FACT_UNITS, FACT_UNITS.UNIT_NAME, FACT_UNITS.UNIT_SYMBOL)
																			.values(unit.getNameProperty().get(), unit.getSymbolProperty().get());
			
			statement.execute();
			
			Main.getLogger().info("Unit created.");
			Main.getLogger().info("Executed SQL: " + statement.getSQL(ParamType.INLINED));
		}
		
		try {
			con.setAutoCommit(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	@Override
	public boolean updateUnits(List<Unit> updates) {
		Connection con = null;
		try {
			con = dslContext.configuration().connectionProvider().acquire();
			con.setAutoCommit(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (Unit unit : updates) {
			UpdateConditionStep<FactUnitsRecord> statement = dslContext
																.update(FACT_UNITS)
																.set(FACT_UNITS.UNIT_NAME, unit.getNameProperty().get())
																.set(FACT_UNITS.UNIT_SYMBOL, unit.getSymbolProperty().get())
																.where(RATIO_CATEGORIES.CATEGORY_ID.equal(unit.getId()));
			
			statement.execute();
			
			Main.getLogger().info("Unit updated.");
			Main.getLogger().info("Executed SQL: " + statement.getSQL(ParamType.INLINED));
		}
		
		try {
			con.setAutoCommit(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	@Override
	public boolean deleteUnits(List<Unit> deletions) {
		Connection con = null;
		try {
			con = dslContext.configuration().connectionProvider().acquire();
			con.setAutoCommit(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (Unit unit : deletions) {
			DeleteConditionStep<FactUnitsRecord> statement = dslContext
																.delete(FACT_UNITS)
																.where(FACT_UNITS.UNIT_ID.equal(unit.getId()));
			
			try {
				statement.execute();
			} catch (DataAccessException e) {
				Main.getLogger().info("Could not delete Unit. Unit possibly still referenced elsewhere.");
				Main.getLogger().info("Executed SQL: " + statement.getSQL(ParamType.INLINED));
				return false;
			}
			
			Main.getLogger().info("Unit deleted.");
			Main.getLogger().info("Executed SQL: " + statement.getSQL(ParamType.INLINED));
		}
		
		try {
			con.setAutoCommit(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
}
