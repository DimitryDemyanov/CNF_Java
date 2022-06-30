
package rcra_p1;

/**
 * @author Dimitry Demyanov and Juan Pablo Martínez (grupo 3.1)
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.regex.Pattern;


class Node {
    String value;
    Node left, right;

    Node(String value, Node left, Node right) {
        this.value = value;
        this.right = right;
        this.left = left;
    }
}

class BinaryTree {
    
    private static boolean binario(String input) {
        return (input.equals("&") || input.equals("|") || input.equals(">") ||
                input.equals("=") || input.equals("%"));
    }
    
    private static boolean not(String input) {
        return (input.equals("-"));
    }
    
    public Node getNode(BinaryTree tree){
        return tree.root;
    }
    
    private ArrayList<String> nodes;
    private int index;
    
    private Node crearArbol() {
        
        Node left;
        Node right;
        
        String s = nodes.get(index);
        
        index++;
        
        if (binario(s)) {
            switch (s) {
                case ">":
                    s = "|";
                    left = new Node("-", crearArbol(), null);
                    right = crearArbol();
                    return new Node(s, left, right);
                    
                case "=":
                    s = "|";
                    left = crearArbol();
                    right = crearArbol();
                    Node not_left = new Node("-", left, null);
                    Node not_right = new Node("-", right, null);
                    Node and = new Node("&", left, right);
                    Node and_not = new Node("&", not_left, not_right);
                    return new Node(s, and, and_not);
                    
                case "%":
                    s = "&";
                    left = crearArbol();
                    right = crearArbol();
                    not_left = new Node("-", left, null);
                    not_right = new Node("-", right, null);
                    Node or = new Node("|", left, right);
                    Node or_not = new Node("|", not_left, not_right);
                    return new Node(s, or, or_not);
                    
                default:
                    left = crearArbol();
                    right = crearArbol();
                    return new Node(s, left, right);
            }
        } else if (not(s)){
            left = crearArbol();
            return new Node(s, left, null);
        } else {
            return new Node(s, null, null);
        }
    }
    
    private Node root;
    private Node a;
    private Node b;
    
    public void add(String[] input) {
        index = 0;
        nodes = new ArrayList<>(Arrays.asList(input));
        root = crearArbol();
    }
    
    public void NNF(Node current) {
        if (current != null) {
            
            String s = current.value;
            
            if (s.equals("-")){
                switch (current.left.value) {
                    case "-": 
                        current.value = current.left.left.value;
                        if (current.left.left.left != null) {
                            current.left = current.left.left.left;
                        } 
                        if (current.left.left.right != null) {
                            current.right = current.right.right.right;
                        } else {
                            current.left = null;
                        }
                        break;
                        
                    case "&":
                        current.value = "|";
                        a = new Node("-",current.left.left, null);
                        b = new Node("-", current.left.right, null);
                        current.left = a;
                        current.right = b;
                        break;
                        
                    case "|":
                        current.value = "&";
                        a = new Node("-",current.left.left, null);
                        b = new Node("-", current.left.right, null);
                        current.left = a;
                        current.right = b;
                        break;
                }
            }
            if (current.left != null) {
                NNF(current.left);
            }
            if (current.right != null) {
                NNF(current.right);
            }
        }
    }
    
    public void concatenarNegaciones(Node current){
        if (current != null){
            
            String s = current.value;
            
            if (s.equals("-")){
                current.value = current.value + current.left.value;
                current.left = null;
            }
            
            concatenarNegaciones(current.left);
            concatenarNegaciones(current.right);
        }
    }
    
    public void traverseLevelOrder() {
        if (root == null) {
            return;
        }

        Queue<Node> nodes = new LinkedList<>();
        nodes.add(root);

        while (!nodes.isEmpty()) {

            Node node = nodes.remove();

            System.out.print(node.value + " ");

            if (node.left != null) {
                nodes.add(node.left);
            }

            if (node.right != null) {
                nodes.add(node.right);
            }
        }
        System.out.println("\n");
    }
    
    public void traverseInOrder(Node node) {
        if (node != null) {
            traverseInOrder(node.left);
            System.out.print(" " + node.value);
            traverseInOrder(node.right);
        }
    }
    
    private ArrayList<ArrayList<String>> cnf;
    
    private ArrayList<ArrayList<String>> CNF(Node current) {
        if (current != null) {
            
            String s = current.value;
            
            ArrayList<ArrayList<String>> aux_l;
            ArrayList<ArrayList<String>> aux_r;
            
            switch (s) {
                case "&":
                    aux_l = CNF(current.left);
                    aux_r = CNF(current.right);
                    aux_l.addAll(aux_r);
                    return aux_l;

                case "|":
                    aux_l = CNF(current.left);
                    aux_r = CNF(current.right);
                    ArrayList<ArrayList<String>> disy_aux = new ArrayList<>();
                    for (ArrayList<String> disy_l : aux_l) {
                        for (ArrayList<String> disy_r : aux_r) {
                            ArrayList<String> elements = new ArrayList<>();
                            elements.addAll(disy_l);
                            elements.addAll(disy_r);
                            disy_aux.add(elements);
                        }
                    }
                    return disy_aux;

                default:
                    ArrayList<ArrayList<String>> aux_cnf = new ArrayList<>();
                    ArrayList<String> aux = new ArrayList<>();
                    aux.add(s);
                    aux_cnf.add(aux);
                    return aux_cnf;
            }
        }
        return cnf;
    }
    
    public ArrayList<ArrayList<String>> doCNF(Node current) {
        cnf = new ArrayList<>();
        cnf.removeAll(cnf);
        return CNF(current);
    }
}

public class RCRA_P1 {

    private static String data = "";
    private static String[] parts;
    private static String[][] words;
    private static ArrayList<String> vocabulary;
    private static String output;

    public static void main(String[] args) {
        
        /* LEER EL ARCHIVO INPUT */
        try {
            
            Path currentPath = Paths.get("");
            String p = currentPath.toAbsolutePath().toString();
            String executionPath = p + "/" + args[0];
            System.out.println(executionPath);
            File myObj = new File(executionPath);
            
            try (Scanner myReader = new Scanner(myObj)) {
                while (myReader.hasNextLine()) {
                    if (data.isEmpty()) {
                        data = myReader.nextLine();
                    } else {
                        data = data + "\r\n" + myReader.nextLine();
                    }
                }
            }
            
            System.out.println("File: " + args[0] + " correctly opened.");
            
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            //e.printStackTrace();
        }
        
        data = data.replace('.', ' '); //Para eliminar los '.'
        
        
        /* PASAR DE STRING A ARRAY(S) */
        parts = data.split("\\r\\n");
        words = new String[parts.length][];
        
        String PATTERN = "\\s";
        Pattern pattern = Pattern.compile(PATTERN);
        
        for (int i = 0; i < parts.length; i++) {
            words[i] = pattern.split(parts[i]);
        }
        
        /* LEER VOCABULARIO */
        vocabulary = new ArrayList<>();
        for (int i = 0; i < parts.length; i++) {
            for (String word : words[i]) {
                if (!(word).equals("&") && !(word).equals("|") && !(word).equals("-") && 
                    !(word).equals(">") && !(word).equals("=") && !(word).equals("%") && 
                    !(word).equals("0") && !(word).equals("1")) {
                    if (!vocabulary.contains(word)) {
                        vocabulary.add(word);
                    }
                }
            }
        }
        
        int size = vocabulary.size();
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            if (i != size-1) {
                sb.append(vocabulary.get(i));sb.append(";");
            } else {
                sb.append(vocabulary.get(i));
            }
        }
        
        output = "{" + sb.toString() + "}.";
        
        /* CONSTRUIR ARBOLES */
        for (int i = 0; i < parts.length; i++) {
            
            output =  output + "\r\n\n" + "% " + parts[i];
            
            BinaryTree tree = new BinaryTree();
            tree.add(words[i]);
            tree.NNF(tree.getNode(tree));
            tree.concatenarNegaciones(tree.getNode(tree));
            ArrayList<ArrayList<String>> cnf = tree.doCNF(tree.getNode(tree));
            
            /* ESCRIBIR OUTPUT EN EL FORMATO CLINGO */
            for (int j = 0; j < cnf.size(); j++) {
                output = output + "\n:- ";
                for (int k = 0; k < cnf.get(j).size(); k++) {
                    String s = cnf.get(j).get(k);
                    if (s.charAt(0) == '-') {
                        s = s.replace("-", "");
                        if (s.equals("0")) {
                            s = "#false";
                        }
                        if (s.equals("1")) {
                            s = "#true";
                        }
                        if (k != cnf.get(j).size()-1) {
                            output = output + s + ", ";
                        } else {
                            output = output + s + " .";
                        }
                    } else {
                        if (s.equals("0")) {
                            s = "#false";
                        }
                        if (s.equals("1")) {
                            s = "#true";
                        }
                        if (k != cnf.get(j).size()-1) {
                            output = output + "not " + s + ", ";
                        } else {
                            output = output + "not " + s + " .";
                        }
                    }
                }
            }
        }
        
        /* DEVOLVER .LP */
        String file = args[0];
        String[] name = file.split("\\.");
        
        Path currentRelativePath = Paths.get("");
        String path = currentRelativePath.toAbsolutePath().toString();
        path = path + "/" + name[0] + ".lp";
        
        try {
            Files.write(Paths.get(path), output.getBytes());
            System.out.println("CONVERSION COMPLETED.");
            
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }
}
