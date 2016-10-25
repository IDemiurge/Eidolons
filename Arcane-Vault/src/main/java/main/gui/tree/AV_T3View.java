package main.gui.tree;

import main.client.cc.CharacterCreator;
import main.client.cc.HC_Master;
import main.client.cc.gui.neo.tree.t3.T3UpperPanel;
import main.client.cc.gui.neo.tree.t3.ThreeTreeView;
import main.client.cc.gui.neo.tree.view.HT_View;
import main.client.dc.Launcher;
import main.content.OBJ_TYPE;
import main.content.OBJ_TYPES;
import main.data.DataManager;
import main.entity.obj.DC_HeroObj;
import main.entity.type.ObjType;
import main.game.DC_Game;
import main.gui.builders.EditViewPanel;
import main.gui.components.controls.ModelManager;
import main.swing.generic.services.dialog.DialogMaster;
import main.system.auxiliary.GuiManager;
import main.system.images.ImageManager;

import java.awt.Dimension;

import javax.swing.JFrame;

public class AV_T3View extends ThreeTreeView {

	private static AV_T3View comp;
	private static AV_T3View classComp;
	private static AV_T3View skillComp;
	private static EditViewPanel table;

	HT_View activeTree;
	HT_View removedTree;

	public AV_T3View(DC_HeroObj hero, Boolean skill_class_spell) {
		super(hero, skill_class_spell);
		// infoPanel.setPanelSize(new Dimension(infoPanel.getPanelSize().width,
		// infoPanel
		// .getPanelSize().height - 40));
		// infoPanel2.setPanelSize(new
		// Dimension(infoPanel2.getPanelSize().width, infoPanel2
		// .getPanelSize().height - 40));
	}

	private void init() {
		// TODO Auto-generated method stub

	}

	/*
	 * or maybe I should return to the idea of letting EDV display a tree! 
	 * 
	 * 
	 * 
	 * bottom panels! 
	 * 
	 * edit table panel!.. 
	 * 
	 * 
	 * selection 
	 * 
	 * sync turn off? 
	 * 
	 * 
	 */
	@Override
	protected void initUpperPanel(DC_HeroObj hero) {
		upperPanel = new T3UpperPanel(T3UpperPanel.AV_CONTROLS, hero, this) {
			public void handleControl(String c, boolean alt) {
				switch (c) {
					case T3UpperPanel.TOGGLE_VIEW:
						if (!alt)
							AV_T3View.toggleEditTable();
						else
							new Thread(new Runnable() {
								public void run() {
									Boolean result = DialogMaster.askAndWait(
											"Where to show the Edit Table?", "Left!", "Right!",
											"Center!");
									AV_T3View.toggleEditTable(result);
								}
							}, "DialogMaster thread").start();

						break;
					case T3UpperPanel.SAVE:
						if (!alt)
							ModelManager.saveAll();
						else
							ModelManager.backUp();
						break;
					case T3UpperPanel.REMOVE:
						ModelManager.backUp();
						break;
					case T3UpperPanel.ADD:
						ModelManager.add(true); // alt ? true : null
						break;
				}
			}

		};
		upperPanel.setPanelSize(new Dimension(treeSize.width, panelSize.height - treeSize.height));
		upperPanel.init();
	}

	public static void toggleEditTable() {
		Boolean left_right_center = null;
		HT_View relevantTree = comp.getRemovedTree();
		if (relevantTree == null)
			relevantTree = comp.getActiveTree();
		if (relevantTree == comp.getLeftTree())
			left_right_center = true;
		if (relevantTree == comp.getRightTree())
			left_right_center = false;

		toggleEditTable(left_right_center);
	}

	public static void toggleEditTable(Boolean left_right_center) {
		// TODO for currently active tree...
		// alt click - let choose, otherwise active...

		String pos = comp.getCenterTreePos();
		if (left_right_center != null) {
			pos = left_right_center ? comp.getLeftTreePos() : comp.getRightTreePos();
		}
		HT_View tree = comp.getCenterTree();
		if (left_right_center != null) {
			tree = left_right_center ? comp.getLeftTree() : comp.getRightTree();
		}
		if (comp.getRemovedTree() != null) {
			comp.remove(table.getPanel());
			comp.add(comp.getRemovedTree(), pos);
			if (left_right_center != null)
				if (left_right_center)
					comp.add(comp.getInfoPanel(), comp.getInfoPanelPos());
				else
					comp.add(comp.getInfoPanel2(), comp.getInfoPanel2Pos());
			comp.setRemovedTree(null);
		} else {
			if (table == null)
				table = new EditViewPanel() {
					// no second table! sync selection!
					public boolean isTwoTableModeEnabled() {
						return false;
					}

					@Override
					public void selectType(ObjType type) {
						resetData(type);
						setTableView();
					}

					protected int getHeight() {
						return (int) comp.getTreeSize().height;
					}

					public boolean isMenuHidden() {
						return true;
					}

					protected int getWidth() {
						return (int) comp.getTreeSize().width;
					}

					protected void initTable(boolean second) {
						super.initTable(second);
						table.setTableHeader(null);
						// scrollPane
						// .setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
					}
				};
			if (HC_Master.getSelectedTreeNode() != null)
				table.selectType(HC_Master.getSelectedTreeNode().getType());
			if (left_right_center != null)
				if (left_right_center)
					comp.remove(comp.getInfoPanel());
				else
					comp.remove(comp.getInfoPanel2());
			comp.remove(tree);
			comp.setRemovedTree(tree);
			comp.add(table.getPanel(), pos);
		}
		comp.revalidate();
		comp.repaint();
	}

	public static void selected(String selectedTypeName, OBJ_TYPE obj_type) {

		if (obj_type == OBJ_TYPES.SKILLS)
			comp = skillComp;
		else
			comp = classComp;
		if (comp == null)
			return;
		comp.selected(DataManager.getType(selectedTypeName, obj_type));

	}

	public void selected(Boolean left_right_none_preferred, ObjType value) {
		Object arg = centerTree.getArg(value.getSubGroupingKey());
		activeTree = centerTree;
		if (arg == leftTree.getArg())
			activeTree = leftTree;
		if (arg == rightTree.getArg())
			activeTree = rightTree;
		if (table != null) {
			table.selectType(value);
			if (removedTree != null) {
				if (removedTree != centerTree)
					left_right_none_preferred = removedTree == rightTree;
			}
		}
		super.selected(left_right_none_preferred, value);
		infoPanel.repaint();
		infoPanel2.repaint();
	}

	public static JFrame showInNewWindow(boolean undecorated, boolean skill_class) {
		GuiManager.setKeyListener(Launcher.getHcKeyListener());

		ObjType type = new ObjType();
		String image = ImageManager.getRandomHeroPortrait();
		type.setImage(image);
		DC_HeroObj hero = new DC_HeroObj(type);// DataManager.getRandomType(OBJ_TYPES.CHARS,
												// null) "Background"
		CharacterCreator.setHero(hero);
		DC_Game.game.getRequirementsManager().setHero(hero);
		comp = new AV_T3View(hero, skill_class);
		if (skill_class) {
			if (skillComp != null) {

			}
			skillComp = comp;
		} else {
			classComp = comp;
		}
		comp.init();
		JFrame window = GuiManager.inNewWindow(undecorated, comp, "T3", GuiManager.getScreenSize());

		// try {
		// window.setUndecorated(true);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		return window;
	}

	public HT_View getActiveTree() {
		return activeTree;
	}

	public void setActiveTree(HT_View activeTree) {
		this.activeTree = activeTree;
	}

	public HT_View getRemovedTree() {
		return removedTree;
	}

	public void setRemovedTree(HT_View removedTree) {
		this.removedTree = removedTree;
	}

}
