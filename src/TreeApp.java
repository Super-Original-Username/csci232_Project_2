/*
    Author: Ethan Fison
        modified from the treeApp program provided on the course webpage
    Date: 2/9/18
    Overview: Reads in a file and performs actions to a binary tree based on the inputs from the file
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Stack;


//////////////////////////////////////////////////////////////
class Node {
    public int iData;           // data item (key)
    public double dData;        // data item
    public Node leftChild;      // this Node's left child
    public Node rightChild;     // this Node's right child
    public int height;         // The balance of the tree as seen by this node
    public int balance;

    public Node(int id, double dd) {
        iData = id;
        dData = dd;
        rightChild = null;
        leftChild = null;
        height = 0;
    }


    public void displayNode() { // display ourself
        System.out.print('{');
        System.out.print(iData);
        System.out.print(", ");
        System.out.print(dData);
        System.out.print("} ");
    }
} // end Class Node
////////////////////////////////////////////////////////////////

class Tree {
    private Node root;                 // first Node of Tree

    public Tree() {                    // constructor
        root = null;                   // no nodes in tree yet
    }


    public void getMin() {
        Node temp = root;
        while (temp.leftChild != null) { // Iterates through to the far left of the tree to find the minimum value
            temp = temp.leftChild;
        }
        temp.displayNode();
        System.out.println();
    }

    public void getMax() {
        Node temp = root;
        while (temp.rightChild != null) { // Iterates through to the right of the tree to find the maximum value
            temp = temp.rightChild;
        }
        temp.displayNode();
        System.out.println();
    }

    public static int getHeight(Node n) {
        if (n == null)
            return 0;
        else
            return 1 + max(getHeight(n.leftChild), getHeight(n.rightChild));
    }

    public static int max(int a, int b) {
        return (a > b) ? a : b;
    }


    public Node find(int key) {      // find node with given key
        Node current = root;         // (assumes non-empty tree)
        while (current.iData != key) {          // while no match
            if (key < current.iData) {          // go left?
                current = current.leftChild;
            } else {                              // or go right?
                current = current.rightChild;
            }
            if (current == null)                 // if no child
            {                                   // didn't find it
                return null;
            }
        }
        return current;                         // found it
    }  //end find()

    public Node insert(Node current, int id, double dd) {
        if (current == null) {
            current = new Node(id, dd); // Creates the new node when the current node is empty
            return current;
        } else if (id < current.iData) {
            current.leftChild = insert(current.leftChild, id, dd); // Recursive call for left child
        } else {
            current.rightChild = insert(current.rightChild, id, dd); // Recursive call for right child
        }
        current.height = 1 + max(getHeight(current.leftChild), getHeight(current.rightChild));
        if (current != null)
            current.balance = checkBalance(current);
        current = doRotations(current);
        return current;
    }

    public int checkBalance(Node c) { // Checks the balance of the current node
        return getHeight(c.rightChild) - getHeight(c.leftChild);
    }

    public Node doRotations(Node c) { // Determines what type of rotation(s) to do for the current node based on its balance
        Node toReturn = null;
        if (c.balance == 2) {
            if (c.rightChild.balance == 1) // Right Right case
                toReturn = rotateLeft(c);
            else if (c.rightChild.balance == -1) { // Right Left case
                c.rightChild = rotateRight(c.rightChild);
                toReturn = rotateLeft(c);
            }
        } else if (c.balance == -2) {
            if (c.leftChild.balance == -1) // Left Left case
                toReturn = rotateRight(c);
            if (c.leftChild.balance == 1) { // Left Right case
                c.leftChild = rotateLeft(c.leftChild);
                toReturn = rotateRight(c);
            }
        } else
            toReturn = c;
        return toReturn;
    }

    public Node rotateLeft(Node GP) { // Takes in the nod eto be rotated, does the rotation, then returns the new grandparent node
        Node pivot = GP.rightChild;
        Node temp = pivot.leftChild;
        pivot.leftChild = GP;
        GP.rightChild = temp;
        GP.height = 1 + max(getHeight(GP.leftChild), getHeight(GP.rightChild));
        pivot.height = 1 + max(getHeight(pivot.leftChild), getHeight(pivot.rightChild));
        return pivot;
    }

    public Node rotateRight(Node GP) { // Takes in the node to be rotated, does the rotation, then returns the new grandparent node
        Node pivot = GP.leftChild;
        Node temp = pivot.rightChild;
        pivot.rightChild = GP;
        GP.leftChild = temp;
        GP.height = 1 + max(getHeight(GP.leftChild), getHeight(GP.rightChild));
        pivot.height = 1 + max(getHeight(pivot.leftChild), getHeight(pivot.rightChild));
        return pivot;
    }


    public void insert(int id, double dd) {
        root = insert(root, id, dd); // Calls new recursive insert method
        System.out.println();
        displayTree(); //Shows the tree after every insertion, for debugging purposes
        System.out.println();
    } // end insert()

    /*public int checkBalance(Node cur, Node par) {
        if (cur == null)
            return -1;
        else
            return Math.max(checkBalance(cur.leftChild, cur), checkBalance(cur.rightChild, cur)) + 1;
    }*/

    // @TODO Figure out how to add the newly rotated current node back to whichever side of its parent it belongs to
    public Boolean delete(Node cur, Node par, int key, boolean isLeft) {
        boolean found = false;
        if (cur.iData != key) {
            if (key < cur.iData) {
                isLeft = true;
                delete(cur.leftChild, cur, key, isLeft); // Recursive delete call for left child
                cur.height = 1 + max(getHeight(cur.leftChild), getHeight(cur.rightChild));
                cur.balance = checkBalance(cur);
                cur = doRotations(cur);
            } else {
                isLeft = false;
                delete(cur.rightChild, cur, key, isLeft); // Recursive delete call for right child
                cur.height = 1 + max(getHeight(cur.leftChild), getHeight(cur.rightChild));
                cur.balance = checkBalance(cur);
                cur = doRotations(cur);
            }
            if (cur == null) // Returns false if the current node doesn't exist
                return false;
        } else
            found = true; // Does this if the node with the matching key has been found
        if (found == true) {
            if (cur.leftChild == null && cur.rightChild == null) { // Case where the node to be deleted has no children
                if (cur == root)
                    root = null;
                else if (isLeft)
                    par.leftChild = null;
                else
                    par.rightChild = null;
            } else if (cur.rightChild == null) { // The node to be deleted has a left child, but not a right child
                if (cur == root)
                    root = cur.leftChild;
                else if (isLeft)
                    par.leftChild = cur.leftChild;
                else
                    par.rightChild = cur.rightChild;
            } else if (cur.leftChild == null) { // The node to be deleted doesn't have a left child node
                if (cur == root)
                    root = cur.rightChild;
                else if (isLeft)
                    par.leftChild = cur.rightChild;
                else
                    par.rightChild = cur.rightChild;
            } else {                    // The node to be deleted has two child nodes
                Node successor = getSuccessor(cur);

                if (cur == root)
                    root = successor;
                else if (isLeft)
                    par.leftChild = successor;
                else
                    par.rightChild = successor;

                successor.leftChild = cur.leftChild;
            }
            par.height -= 1;
        }
        root = root;
        return true;
    }


    public boolean delete(int key) {             // delete node with given key
        Node current = root;                     // (assumes non-empty list)
        Node parent = root;
        boolean isLeftChild = true;
        return delete(current, parent, key, isLeftChild);
    }// end delete()


    //returns node with next-highest value after delNode
    //goes right child, then right child's left descendants
    private Node getSuccessor(Node delNode) {
        Node successorParent = delNode;
        Node successor = delNode;
        Node current = delNode.rightChild;        // go to the right child
        while (current != null) {                 // until no more
            successorParent = successor;          // left children
            successor = current;
            current = current.leftChild;
        }

        if (successor != delNode.rightChild) {    // if successor not right child,
            //make connections
            successorParent.leftChild = successor.rightChild;
            successor.rightChild = delNode.rightChild;
        }
        return successor;
    }


    public void traverse(int traverseType) {
        switch (traverseType) {
            case 1:
                System.out.print("Preorder traversal: ");
                preOrder(root);
                break;
            case 2:
                System.out.print("Inorder traversal: ");
                inOrder(root);
                break;
            case 3:
                System.out.print("Postorder traversal: ");
                postOrder(root);
                break;
            default:
                System.out.print("Invalid traversal type\n");
                break;
        }
        System.out.println();
    }


    private void preOrder(Node localRoot) {
        if (localRoot != null) {
            System.out.print(localRoot.iData + " ");
            preOrder(localRoot.leftChild);
            preOrder(localRoot.rightChild);
        }
    }


    private void inOrder(Node localRoot) {
        if (localRoot != null) {
            inOrder(localRoot.leftChild);
            System.out.print(localRoot.iData + " ");
            inOrder(localRoot.rightChild);
        }
    }


    private void postOrder(Node localRoot) {
        if (localRoot != null) {
            postOrder(localRoot.leftChild);
            postOrder(localRoot.rightChild);
            System.out.print(localRoot.iData + " ");
        }
    }


    public void displayTree() {
        Stack<Node> globalStack = new Stack<Node>();
        globalStack.push(root);
        int nBlanks = 32;
        boolean isRowEmpty = false;
        System.out.println(
                ".................................................................");
        while (isRowEmpty == false) {
            Stack<Node> localStack = new Stack<Node>();
            isRowEmpty = true;

            for (int j = 0; j < nBlanks; j++) {
                System.out.print(' ');
            }

            while (globalStack.isEmpty() == false) {
                Node temp = (Node) globalStack.pop();
                if (temp != null) {
                    System.out.print(temp.iData);
                    localStack.push(temp.leftChild);
                    localStack.push(temp.rightChild);
                    if (temp.leftChild != null ||
                            temp.rightChild != null) {
                        isRowEmpty = false;
                    }
                } else {
                    System.out.print("--");
                    localStack.push(null);
                    localStack.push(null);
                }

                for (int j = 0; j < nBlanks * 2 - 2; j++) {
                    System.out.print(' ');
                }
            } // end while globalStack not empty
            System.out.println();
            nBlanks /= 2;
            while (localStack.isEmpty() == false) {
                globalStack.push(localStack.pop());
            } // end while isRowEmpty is false
            System.out.println(
                    ".................................................................");
        } // end displayTree()
    } // end class Tree
}
////////////////////////////////////////////////////////////////

class TreeApp {

    public static void main(String[] args) throws IOException {
        Path file = Paths.get("input/input.txt");
        Charset charset = Charset.forName("US-ASCII"); // Declares the character set to be used by the bufferedreader
        int value;

        Tree theTree = new Tree();
        try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
            String line = null;
            int[] k;
            while ((line = reader.readLine()) != null) {
                String[] csv = line.split("[,\\s]+"); // Converts the line into a string array
                String action = csv[0]; // Takes the first item in the array and assigns that to the action to be performed
                k = new int[csv.length - 1]; // Declares a new array with a length 1 shorter than the string array
                for (int i = 1; i < csv.length; i++) {
                    k[i - 1] = Integer.parseInt(csv[i]); // Parses through the string array and converts any ints into the newly made int array
                }
                switch (action) {
                    case "show":
                        theTree.displayTree();
                        break;

                    case "insert":
                        System.out.print("Inserting: ");
                        for (int i = 0; i < k.length; i++) { // Loops through the int array and inserts each number provided into the tree
                            System.out.print(k[i] + ", ");
                            theTree.insert(k[i], k[i] + 0.9);
                        }
                        System.out.println();
                        break;

                    case "find":
                        Node found = theTree.find(k[0]);
                        if (found != null) {
                            System.out.print("Found: ");
                            found.displayNode();
                            System.out.print("\n");
                        } else {
                            System.out.print("Could not find ");
                            System.out.println(k[0]);
                        }
                        break;

                    case "delete":
                        boolean didDelete = theTree.delete(k[0]);
                        if (didDelete) {
                            System.out.print("Deleted: " + k[0] + '\n');
                        } else {
                            System.out.print("Could not delete value: " + k[0] + '\n');
                        }
                        break;

                    case "traverse":
                        theTree.traverse(k[0]);
                        break;

                    case "min":
                        theTree.getMin();
                        break;

                    case "max":
                        theTree.getMax();
                        break;

                    default:
                        System.out.print("Invalid entry\n");
                        break;
                }

            }
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }
    } // end main()


    private static String getString() throws IOException {
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        String s = br.readLine();
        return s;
    }

    private static int getChar() throws IOException {
        String s = getString();
        return s.charAt(0);
    }

    private static int getInt() throws IOException {
        String s = getString();
        return Integer.parseInt(s);
    }
}  // end TreeApp class
////////////////////////////////////////////////////////////////

