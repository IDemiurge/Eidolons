package main.gui.tree;

import main.content.OBJ_TYPE;
import main.entity.type.ObjType;
import main.handlers.AvHandler;
import main.handlers.AvManager;

import java.util.*;
import java.util.stream.Collectors;

public class AvTreeBuilder extends AvHandler {
    private Comparator<ObjType> typeSorter;
    private Comparator<Object> groupSorter;

    /*
            Workspaces ?
             */
    public AvTreeBuilder(AvManager manager) {
        super(manager);
    }

    // public AvTreeNode build( OBJ_TYPE TYPE, String group) {
    //     return build(  TYPE, group, typeSorter,
    //               groupSorter );
    // }
    public AvTreeNode build(Collection<ObjType> types, OBJ_TYPE TYPE, String group,
                            Comparator<ObjType> comparator, Comparator<Object> groupSorter) {
        // Set<ObjType> types = getTypeHandler().getParentTypes(TYPE);
        return build(types, TYPE, group, comparator, null , groupSorter);
    }
    public AvTreeNode build(Collection<ObjType> types, OBJ_TYPE TYPE, String group, Comparator<ObjType> comparator,
                            Collection<String> subgroups, Comparator<Object> groupSorter) {

        Set<ObjType> parentTypes = getTypeHandler().getParentTypes(TYPE);
        types = new ArrayList<>(types);
        types.removeIf(type -> !parentTypes.contains(type));

        return build(toAvNodeTree(group, types, subgroups), comparator, groupSorter);
        /*
        could we have some duplication?
        could we have some other nodes of use?
        support re-build on demand, and quickly
         */
    }

    private AvTreeNode build(AvNode avNode, Comparator<ObjType> comparator, Comparator<Object> groupSorter) {
        AvTreeNode root = new AvTreeNode(avNode.userObject);
        Set<AvNode> nodes =   avNode.children.stream().sorted((o1, o2) -> {
                if (o1.userObject instanceof ObjType) {
                    return
                            comparator.compare((ObjType) o1.userObject, (ObjType) o2.userObject);
                } return
                        groupSorter.compare( o1.userObject,  o2.userObject);
            }).
                    collect(Collectors.toCollection(() -> new LinkedHashSet<>()));

        for (AvNode node : nodes) {
            root.add(build(node, comparator, groupSorter));
        }
        return root;
    }

    private AvNode toAvNodeTree(String group, Collection<ObjType> parentTypes, Collection<String> subgroups //sorted!
    ) {
        Set<AvNode> subGroupNodes = new LinkedHashSet<>();
        AvNode root = new AvNode(group, subGroupNodes);
        if (subgroups == null) {
            subgroups = new LinkedHashSet<>();
            for (ObjType parentType : parentTypes) {
                subgroups.add(parentType.getSubGroupingKey());
            }
        }
        for (String subgroup : subgroups) {
            Set<AvNode> nodes = constructNodes(parentTypes.stream().
                    filter(type -> getCheckHandler().subgroup(type, subgroup)).collect(Collectors.toSet()));
            subGroupNodes.add(new AvNode(subgroup, nodes));
        }
        return root;
    }

    private Set<AvNode> constructNodes( Set<ObjType> types) {
        //parent* types from this subgroup only
        Set<AvNode> set = new LinkedHashSet<>();

        for (ObjType type : types) {
            Set<ObjType> subs = getTypeHandler().getSubTypes(type);
            set.add(new AvNode(type, constructNodes(subs)));
        }

        return set;
    }

}













