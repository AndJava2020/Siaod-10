package com.company;

import java.util.Formatter;
import java.util.Scanner;

public class RedBlackTree {

    private final int RED = 0;
    private final int BLACK = 1;

    private class Node { //конструктор узла
        int key = -1, color = BLACK;
        Node left = zero, right = zero, parent = zero;

        Node(int key) {
            this.key = key;
        }
    }

    private final Node zero = new Node(-1);
    private Node root = zero;

    public void printTree(Node node) {
        if (node == zero) {
            return;
        }
        printTree(node.left);
        System.out.print(((node.color==RED)?"Color: RED ":"Color: BLACK ")+"KEY: "+node.key+" Parent node: "+node.parent.key+"\n");
        printTree(node.right);
    }

    private Node findNode(Node findNode, Node node) { //поиск узла
        if (root == zero) {
            return null;
        }
        if (findNode.key < node.key) {
            if (node.left != zero) {
                return findNode(findNode, node.left);
            }
        } else if (findNode.key > node.key) {
            if (node.right != zero) {
                return findNode(findNode, node.right);
            }
        } else if (findNode.key == node.key) {
            return node;
        }
        return null;
    }

    private void insert(Node node) { //вставить узел
        Node temp = root;
        if (root == zero) {
            root = node;
            node.color = BLACK;
            node.parent = zero;
        } else {
            node.color = RED;
            while (true) {
                if (node.key < temp.key) {
                    if (temp.left == zero) {
                        temp.left = node;
                        node.parent = temp;
                        break;
                    } else {
                        temp = temp.left;
                    }
                } else if (node.key >= temp.key) {
                    if (temp.right == zero) {
                        temp.right = node;
                        node.parent = temp;
                        break;
                    } else {
                        temp = temp.right;
                    }
                }
            }
            fixTree(node); //перебалансировка после вставки
        }
    }

    private void fixTree(Node node) { //перебалансировка после вставки
        while (node.parent.color == RED) {
            Node uncle = zero;
            if (node.parent == node.parent.parent.left) {
                uncle = node.parent.parent.right;

                if (uncle != zero && uncle.color == RED) {
                    node.parent.color = BLACK;
                    uncle.color = BLACK;
                    node.parent.parent.color = RED;
                    node = node.parent.parent;
                    continue;
                }
                if (node == node.parent.right) {
                    node = node.parent;
                    rotateLeft(node);
                }
                node.parent.color = BLACK;
                node.parent.parent.color = RED;
                rotateRight(node.parent.parent);
            } else {
                uncle = node.parent.parent.left;
                if (uncle != zero && uncle.color == RED) {
                    node.parent.color = BLACK;
                    uncle.color = BLACK;
                    node.parent.parent.color = RED;
                    node = node.parent.parent;
                    continue;
                }
                if (node == node.parent.left) {
                    node = node.parent;
                    rotateRight(node);
                }
                node.parent.color = BLACK;
                node.parent.parent.color = RED;
                rotateLeft(node.parent.parent);
            }
        }
        root.color = BLACK;
    }

    void rotateLeft(Node node) {
        if (node.parent != zero) {
            if (node == node.parent.left) {
                node.parent.left = node.right;
            } else {
                node.parent.right = node.right;
            }
            node.right.parent = node.parent;
            node.parent = node.right;
            if (node.right.left != zero) {
                node.right.left.parent = node;
            }
            node.right = node.right.left;
            node.parent.left = node;
        } else {
            Node right = root.right;
            root.right = right.left;
            right.left.parent = root;
            root.parent = right;
            right.left = root;
            right.parent = zero;
            root = right;
        }
    }

    void rotateRight(Node node) {
        if (node.parent != zero) {
            if (node == node.parent.left) {
                node.parent.left = node.left;
            } else {
                node.parent.right = node.left;
            }

            node.left.parent = node.parent;
            node.parent = node.left;
            if (node.left.right != zero) {
                node.left.right.parent = node;
            }
            node.left = node.left.right;
            node.parent.right = node;
        } else {
            Node left = root.left;
            root.left = root.left.right;
            left.right.parent = root;
            root.parent = left;
            left.right = root;
            left.parent = zero;
            root = left;
        }
    }

    void deleteTree(){// очистить
        root = zero;
    }

    void transplant(Node target, Node with){ // заменить узел
        if(target.parent == zero){
            root = with;
        }else if(target == target.parent.left){
            target.parent.left = with;
        }else
            target.parent.right = with;
        with.parent = target.parent;
    }

    boolean delete(Node z){//удалить элемент
        if((z = findNode(z, root))==null)return false;
        Node x;
        Node y = z;
        int y_original_color = y.color;

        if(z.left == zero){
            x = z.right;
            transplant(z, z.right);
        }else if(z.right == zero){
            x = z.left;
            transplant(z, z.left);
        }else{
            y = treeMinimum(z.right);
            y_original_color = y.color;
            x = y.right;
            if(y.parent == z)
                x.parent = y;
            else{
                transplant(y, y.right);
                y.right = z.right;
                y.right.parent = y;
            }
            transplant(z, y);
            y.left = z.left;
            y.left.parent = y;
            y.color = z.color;
        }
        if(y_original_color==BLACK)
            deleteFixup(x);
        return true;
    }

    void deleteFixup(Node x){ //перебалансировка дерева после удаления
        while(x!=root && x.color == BLACK){
            if(x == x.parent.left){
                Node w = x.parent.right;
                if(w.color == RED){
                    w.color = BLACK;
                    x.parent.color = RED;
                    rotateLeft(x.parent);
                    w = x.parent.right;
                }
                if(w.left.color == BLACK && w.right.color == BLACK){
                    w.color = RED;
                    x = x.parent;
                    continue;
                }
                else if(w.right.color == BLACK){
                    w.left.color = BLACK;
                    w.color = RED;
                    rotateRight(w);
                    w = x.parent.right;
                }
                if(w.right.color == RED){
                    w.color = x.parent.color;
                    x.parent.color = BLACK;
                    w.right.color = BLACK;
                    rotateLeft(x.parent);
                    x = root;
                }
            }else{
                Node w = x.parent.left;
                if(w.color == RED){
                    w.color = BLACK;
                    x.parent.color = RED;
                    rotateRight(x.parent);
                    w = x.parent.left;
                }
                if(w.right.color == BLACK && w.left.color == BLACK){
                    w.color = RED;
                    x = x.parent;
                    continue;
                }
                else if(w.left.color == BLACK){
                    w.right.color = BLACK;
                    w.color = RED;
                    rotateLeft(w);
                    w = x.parent.left;
                }
                if(w.left.color == RED){
                    w.color = x.parent.color;
                    x.parent.color = BLACK;
                    w.left.color = BLACK;
                    rotateRight(x.parent);
                    x = root;
                }
            }
        }
        x.color = BLACK;
    }

    Node treeMinimum(Node subTreeRoot){ //минимум
        while(subTreeRoot.left!=zero){
            subTreeRoot = subTreeRoot.left;
        }
        return subTreeRoot;
    }

    public void consoleUI() { //интерфейс
        Scanner scan = new Scanner(System.in);
        while (true) {
            System.out.println("\n1) add node\n"
                    + "2) delete node\n"
                    + "3) find node\n"
                    + "4) print tree\n"
                    + "5) delete tree\n"
                    + "6) paint tree\n");
            int choice = scan.nextInt();
            int item;
            Node node = null;
            switch (choice) {
                case 1:
                    item = scan.nextInt();
                    while (item != -999) {
                        node = new Node(item);
                        insert(node);
                        item = scan.nextInt();
                    }
                    printTree(root);
                    break;
                case 2:
                    item = scan.nextInt();
                    while (item != -999) {
                        node = new Node(item);
                        System.out.print("\ndeleting" + item);
                        if (delete(node)) {
                            System.out.print(": deleted");
                        } else {
                            System.out.print(": err");
                        }
                        item = scan.nextInt();
                    }
                    System.out.println();
                    printTree(root);
                    break;
                case 3:
                    item = scan.nextInt();
                    while (item != -999) {
                        node = new Node(item);
                        System.out.println((findNode(node, root) != null) ? "found" : "err");
                        item = scan.nextInt();
                    }
                    break;
                case 4:
                    printTree(root);
                    break;
                case 5:
                    deleteTree();
                    break;
                case 6:
                    System.out.println("Верхушка дерева слева.\nБратья, сёстры - на одном уровне, правый ребёнок(меньший) выше, левый ребёнок(больший) ниже по столбцам.\nСами дети выше родителя \nВ скобках: Красный - 0, чёрный - 1");
                    print_tree(root,0);

            }
        }
    }

    void print_tree(Node node,int l) { //нарисовать дерево

        if (node != zero) {
            if (node.left!=zero) print_tree(node.left, l+4);
            if(node==root) System.out.println();
            if (node.right!=zero) print_tree(node.right, l + 4);
            if (l>0) {
                setwL(" ",l);
                System.out.print("   ");

            }
            System.out.println(node.key +"(" + node.color +")");
        }
    }

    public void setwL(String str, int width){
        for (int x = str.length(); x < width; ++x)
        {
            System.out.print(' ');
        }
        System.out.print(str);
    }

    public static void main(String[] args) {
        RedBlackTree rbt = new RedBlackTree();
        rbt.consoleUI();
    }
}
