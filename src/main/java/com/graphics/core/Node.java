package com.graphics.core;

import java.util.ArrayList;
import java.util.List;

// Nodo base con ciclo de vida simple para escena.
public class Node {

    private String name;
    private Node parent;
    private final List<Node> children = new ArrayList<>();
    private boolean ready = false;
    private boolean queuedForFree = false;

    public Node() {
        this.name = getClass().getSimpleName();
    }

    public Node(String name) {
        this.name = name;
    }

    // Agrega un hijo. Si el padre ya esta listo, el hijo entra al arbol al instante.
    public void addChild(Node child) {
        child.parent = this;
        children.add(child);
        if (ready) {
            child.enterTree();
        }
    }

    // Remueve un hijo del arbol.
    public void removeChild(Node child) {
        children.remove(child);
        child.parent = null;
    }

    // Marca este nodo para eliminarse al final del frame.
    public void queueFree() {
        queuedForFree = true;
    }

    // Inicia el arbol: llama _ready() en cascada.
    public void enterTree() {
        if (!ready) {
            _ready();
            ready = true;
        }
        for (Node child : children) {
            child.enterTree();
        }
    }

    // Actualiza el arbol: llama _process() en cascada y limpia nodos marcados.
    public void processTree(float delta) {
        _process(delta);
        // Procesar hijos (copia para evitar ConcurrentModification).
        List<Node> snapshot = new ArrayList<>(children);
        for (Node child : snapshot) {
            if (!child.queuedForFree) {
                child.processTree(delta);
            }
        }
        pruneQueuedChildren();
    }

    // Renderiza el arbol: llama _render() en cascada.
    public void renderTree(Renderer r) {
        _render(r);
        for (Node child : children) {
            child.renderTree(r);
        }
    }

    // Metodos para sobreescribir en las clases.
    // _ready() se llama una vez cuando el nodo entra al arbol.
    public void _ready() {}
    // _process() se llama cada frame para logica y fisica.
    public void _process(float delta) {}
    // _render() se llama cada frame para dibujar.
    public void _render(Renderer r) {}

    // Busca el primer hijo de un tipo especifico.
    @SuppressWarnings("unchecked")
    public <T extends Node> T getChild(Class<T> type) {
        for (Node child : children) {
            if (type.isInstance(child)) return (T) child;
        }
        return null;
    }

    // Busca todos los hijos de un tipo especifico.
    @SuppressWarnings("unchecked")
    public <T extends Node> List<T> getChildren(Class<T> type) {
        List<T> result = new ArrayList<>();
        for (Node child : children) {
            if (type.isInstance(child)) result.add((T) child);
        }
        return result;
    }

    private void pruneQueuedChildren() {
        children.removeIf(c -> c.queuedForFree);
    }

    // Getters.

    public String getName()         { return name; }
    public Node getParent()         { return parent; }
    public List<Node> getChildren() { return children; }
}
