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

    public Node(int id, double dd) {
        iData = id;
        dData = dd;
        rightChild = null;
        leftChild = null;
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
        return current;
    }

    public void insert(int id, double dd) {
        root = insert(root, id, dd); // Calls new recursive insert method
    } // end insert()


    public boolean delete(int key) {             // delete node with given key
        Node current = root;                     // (assumes non-empty list)
        Node parent = root;
        boolean isLeftChild = true;

        while (current.iData != key) {           // search for Node
            parent = current;
            if (key < current.iData) {           // go left?
                isLeftChild = true;
                current = current.leftChild;
            } else {                               // or go right?
                isLeftChild = false;
                current = current.rightChild;
            }
            if (current == null) {                // end of the line,
                return false;                    // didn't find it
            }
        }
        //found the node to delete

        //if no children, simply delete it
        if (current.leftChild == null && current.rightChild == null) {
            if (current == root) {              // if root,
                root = null;                    // tree is empty
            } else if (isLeftChild) {
                parent.leftChild = null;        // disconnect
            }                                   // from parent
            else {
                parent.rightChild = null;
            }
        }
        //if no right child, replace with left subtree
        else if (current.rightChild == null) {
            if (current == root) {
                root = current.leftChild;
            } else if (isLeftChild) {
                parent.leftChild = current.leftChild;
            } else {
                parent.rightChild = current.leftChild;
            }
        }

        //if no left child, replace with right subtree
        else if (current.leftChild == null) {
            if (current == root) {
                root = current.rightChild;
            } else if (isLeftChild) {
                parent.leftChild = current.rightChild;
            } else {
                parent.rightChild = current.rightChild;
            }
        } else { // two children, so replace with inorder successor
            // get successor of node to delete (current)
            Node successor = getSuccessor(current);

            // connect parent of current to successor instead
            if (current == root) {
                root = successor;
            } else if (isLeftChild) {
                parent.leftChild = successor;
            } else {
                parent.rightChild = successor;
            }

            //connect successor to current's left child
            successor.leftChild = current.leftChild;
        } // end else two children
        // (successor cannot have a left child)
        return true;              // success
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

