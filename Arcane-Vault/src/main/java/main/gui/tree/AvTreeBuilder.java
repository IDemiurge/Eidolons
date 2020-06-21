package main.gui.tree;

import com.google.inject.internal.util.ImmutableSet;
import main.entity.type.ObjType;
import main.handlers.AvHandler;
import main.handlers.AvManager;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class AvTreeBuilder extends AvHandler {
    /*
    Workspaces ?
     */
    public AvTreeBuilder(AvManager manager) {
        super(manager);
    }

    public AvTreeNode build(Set<ObjType> types, String group, Comparator<ObjType> comparator) {
        return build(toAvNodeTree(group, types), comparator);
        /*
        could we have some duplication?
        could we have some other nodes of use?
        support re-build on demand, and quickly
         */
    }

    private AvTreeNode build(AvNode avNode, Comparator<ObjType> comparator) {
        AvTreeNode root = new AvTreeNode(avNode.userObject);
        Set<AvNode> nodes = avNode.children.stream().sorted((o1, o2) ->
                comparator.compare((ObjType) o1.userObject, (ObjType) o2.userObject)).
                collect(Collectors.toCollection(() -> new LinkedHashSet<>()));

        for (AvNode node : nodes) {
            root.add(build(node, comparator));
        }
        return root;
    }

    private AvNode toAvNodeTree(String group, Set<ObjType> parentTypes) {
        Set<AvNode> subGroupNodes = new LinkedHashSet<>();
        AvNode root = new AvNode(group, subGroupNodes);
        Set<String> subgroups = null;
        for (String subgroup : subgroups) {
            Set<AvNode> nodes = constructNodes(subgroup, parentTypes.stream().
                    filter(type -> getCheckHandler().subgroup(type, subgroup)).collect(Collectors.toSet()));
            subGroupNodes.add(new AvNode(subgroup, nodes));
        }
        return root;
    }

    private Set<AvNode> constructNodes(Object object, Set<ObjType> types) {
        //parent* types from this subgroup only
        Set<AvNode> set = new LinkedHashSet<>();

        for (ObjType type : types) {
            Set<ObjType> subs = getTypeHandler().getSubTypes(type);
            set.add(new AvNode(type, constructNodes(type, subs)));
        }

        if (object instanceof String)
            return ImmutableSet.of(new AvNode(object, set));

        return set;
    }

}













