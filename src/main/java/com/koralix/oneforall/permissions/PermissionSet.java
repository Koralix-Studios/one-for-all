package com.koralix.oneforall.permissions;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PermissionSet implements Set<String> {
    private final Map<String, PermissionSet> children = new HashMap<>();

    @Override
    public int size() {
        int size = children.size();
        for (PermissionSet child : children.values()) {
            size += child.size();
        }
        return size;
    }

    @Override
    public boolean isEmpty() {
        return children.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        if (!(o instanceof String str)) return false;
        if (children.containsKey(str) || children.containsKey("*")) return true;
        for (PermissionSet child : children.values()) {
            if (child.contains(str)) return true;
        }
        return false;
    }

    @NotNull
    @Override
    public Iterator<String> iterator() {
        return new Iterator<>() {
            private final Stack<Iterator<Map.Entry<String, PermissionSet>>> stack;
            private final Stack<String> pathStack;
            private String nextPath;

            {
                stack = new Stack<>();
                pathStack = new Stack<>();
                stack.push(children.entrySet().iterator());
                advance();
            }

            private void advance() {
                nextPath = null;
                while (!stack.isEmpty()) {
                    Iterator<Map.Entry<String, PermissionSet>> iterator = stack.peek();
                    if (!iterator.hasNext()) {
                        stack.pop();
                        if (!pathStack.isEmpty()) {
                            pathStack.pop();
                        }
                        continue;
                    }

                    Map.Entry<String, PermissionSet> entry = iterator.next();
                    String key = entry.getKey();

                    if (!pathStack.isEmpty()) {
                        pathStack.push(pathStack.peek() + "." + key);
                    } else {
                        pathStack.push(key);
                    }

                    nextPath = pathStack.peek();
                    stack.push(entry.getValue().children.entrySet().iterator());
                    return;
                }
            }

            @Override
            public boolean hasNext() {
                return nextPath != null;
            }

            @Override
            public String next() {
                if (!hasNext()) throw new NoSuchElementException("No more elements");
                String path = nextPath;
                advance();
                return path;
            }
        };
    }

    @NotNull
    @Override
    public Object @NotNull [] toArray() {
        return children.keySet().toArray();
    }

    @NotNull
    @Override
    public <T> T @NotNull [] toArray(@NotNull T @NotNull [] a) {
        return children.keySet().toArray(a);
    }

    @Override
    public boolean add(String s) {
        if (s.isEmpty()) return false;
        String[] parts = s.split("\\.", 2);
        if (parts.length == 1) {
            return children.putIfAbsent(parts[0], new PermissionSet()) == null;
        } else {
            PermissionSet child = children.computeIfAbsent(parts[0], k -> new PermissionSet());
            return child.add(parts[1]);
        }
    }

    @Override
    public boolean remove(Object o) {
        if (!(o instanceof String str)) return false;
        String[] parts = str.split("\\.", 2);
        if (parts.length == 1) {
            return children.remove(parts[0]) != null;
        } else {
            PermissionSet child = children.get(parts[0]);
            if (child == null) return false;
            boolean removed = child.remove(parts[1]);
            if (child.isEmpty()) {
                children.remove(parts[0]);
            }
            return removed;
        }
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        for (Object o : c) {
            if (!contains(o)) return false;
        }
        return true;
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends String> c) {
        boolean modified = false;
        for (String s : c) {
            modified |= add(s);
        }
        return modified;
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        List<String> toRemove = new ArrayList<>();
        for (String s : this) {
            if (!c.contains(s)) {
                toRemove.add(s);
            }
        }
        return removeAll(toRemove);
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        boolean modified = false;
        for (Object o : c) {
            modified |= remove(o);
        }
        return modified;
    }

    @Override
    public void clear() {
        children.clear();
    }
}
