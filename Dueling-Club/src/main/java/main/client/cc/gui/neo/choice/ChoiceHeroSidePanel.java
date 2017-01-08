package main.client.cc.gui.neo.choice;

public class ChoiceHeroSidePanel {
    /*
	 * The idea is to let the player see what he's crafting :) So in essence,
	 * how is it different from the final-view HeroPanel?
	 * 
	 * 1) Will be dealing with an incomplete HeroObject >> For TemplateView - we
	 * will be applying the template to a dummy Obj and displaying it...
	 * 
	 * 2) Will be to update based on *user selection* >> E.g., Deity Bonuses,
	 * Emblem, Portrait
	 * 
	 * 
	 * Also, consider the FullInfoPanel... I imagine it should take all or most
	 * of the space vertically... There would be large texts there at some
	 * choices - deity descriptions, background descriptions... These will have
	 * to be fairly large in size to ensure there are no more than 2 pages per
	 * descr.
	 * 
	 * Then there is the question of Companion Choice... I could display a full
	 * HeroPanel for those I suppose :)
	 * 
	 * So as class, how is *this* different from HeroPanel?
	 * 
	 * Well, it could simply have a different initComps! It does not require
	 * displaying classes... It should display stats - at least attributes, and
	 * certainly have a Mastery page
	 * 
	 * double info panel 1) Different pages or same? 2) Ideal header: props,
	 * attrs, passives 3) Full info panel below
	 * 
	 * As for DeityInfoPanel...
	 * 
	 * 
	 * And the template IP: GENERAL: display "final" values? Or display offsets
	 * and apply them to the HERO TOP: stats, masteries, resistances, specials
	 * BOT: description/lore
	 */
}
