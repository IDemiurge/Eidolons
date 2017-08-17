package main.system.text;

import main.system.auxiliary.data.ListMaster;
import main.system.graphics.ANIM;
import main.system.graphics.AnimPhase.PHASE_TYPE;
import main.system.launch.CoreEngine;
import main.system.text.EntryNodeMaster.ENTRY_TYPE;

import java.util.LinkedList;
import java.util.List;

public class LogEntryNode {

    LogEntryNode parent;
    List<LogEntryNode> children;
    String header;
    List<String> entries = new LinkedList<>();
    List<PHASE_TYPE> animPhasesToPlay;
    private ENTRY_TYPE type;
    private int lineIndex;
    private Object[] args;
    private int pageIndex;
    private ANIM linkedAnimation;
    private List<ANIM> linkedAnimations;

    public LogEntryNode(LogEntryNode parent, ENTRY_TYPE type, int i, boolean logLater,
                        Object... args) {
        this.type = type;
        this.lineIndex = i;
        this.parent = parent;
        if (args != null) {
            if (!logLater) {
                initHeader(args);
            }
        }
    }

    public String getButtonImagePath() {
        return type.getButtonImagePath();
    }

    public void initHeader(Object[] args) {
        this.args = args;
        String[] strings = ListMaster.toStringList(false, args).toArray(new String[args.length]);
        header = EntryNodeMaster.getHeader(type, strings);

        entries.add(header);
    }

    public void addEntry(LogEntryNode node) {

        for (String s : EntryNodeMaster.getStringsFromSubNode(node)) {
            addString(s);
        }
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public void addString(String string) {
        if (CoreEngine.isGraphicsOff())
            return;
        List<String> lines = TextWrapper.wrap(string, EntryNodeMaster.getWrapLength(false));
        if (lines.size() > EntryNodeMaster.getMaxLinesPerHeader()) {
            ListMaster.cropLast(lines, lines.size() - EntryNodeMaster.getMaxLinesPerHeader());
            lines.set(EntryNodeMaster.getMaxLinesPerHeader() - 1, lines.get(EntryNodeMaster
                    .getMaxLinesPerHeader() - 1)
                    + EntryNodeMaster.CROP_SUFFIX);
        }
        entries.addAll(lines);
        // track subnode page displayed line count!!! TODO
        // Game.game.getLogManager().addedLineToSubNodeEntry(lines.size());

    }

    @Override
    public String toString() {

        return "Entry: " + getHeader() + " at " + getLineIndex() + " line" + " of "
                + getPageIndex() + " page";
    }

    // INFO_LEVEL info_level ?
    public List<String> getTextLines() {
        return entries;
    }

    public List<LogEntryNode> getChildren() {
        return children;
    }

    public ENTRY_TYPE getType() {
        return type;
    }

    public String getHeader() {
        return header;
    }

    public List<String> getEntries() {
        return entries;
    }

    public LogEntryNode getParent() {
        return parent;
    }

    public int getLineIndex() {
        return lineIndex;
    }

    public Object[] getArgs() {
        return args;
    }

    public Object getLinkedAnimation() {
        if (linkedAnimation == null) {
            if (!getLinkedAnimations().isEmpty()) {
                linkedAnimation = linkedAnimations.get(0);
            }
        }
        return linkedAnimation;
    }

    public void setLinkedAnimation(ANIM linkedAnimationKey) {
        this.linkedAnimation = linkedAnimationKey;
    }

    public void setAnimPhasesToPlay(PHASE_TYPE... animPhasesToPlay) {
        // Arrays.asList(a)
        // setAnimPhasesToPlay();
    }

    public void addLinkedAnimations(ANIM... anims) {
        // addLinkedAnimations(Arrays.asList(anims));
    }

    public void addLinkedAnimations(List<ANIM> list) {
        if (list != null) {
            getLinkedAnimations().addAll(list);
        }
    }

    public List<ANIM> getLinkedAnimations() {
        if (linkedAnimations == null) {
            linkedAnimations = new LinkedList<>();
            if (linkedAnimation != null) {
                linkedAnimations.add(linkedAnimation);
            }
        }
        return linkedAnimations;
    }

    public List<PHASE_TYPE> getAnimPhasesToPlay() {
        return animPhasesToPlay;
    }

    public void setAnimPhasesToPlay(List<PHASE_TYPE> animPhasesToPlay) {
        this.animPhasesToPlay = animPhasesToPlay;
    }

}
