/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HotPlate;

import java.util.Collections;

/**
 *
 * @author ricjo
 */

// Can use any generic type as long as it extends to comparable.
public class LinkedRRSet<E extends Comparable<E>> extends LinkedSet<E> {

    public LinkedRRSet() {
        super();
    }

    // o.compareTo(currentNode.element) < 0) = if element about to be added is smaller than element being compared with
    @Override
    public boolean add(E o) {

        // Check if dupliate
        if (!(contains(o))) {

            Node<E> newNode = new Node<E>(o);
            Node<E> currentNode = firstNode;

            if (firstNode == null || o.compareTo(firstNode.element) < 0) {
                newNode.next = firstNode;
                firstNode = newNode;
                numElements++;
                return true;
            } else {
                while(currentNode.next != null && o.compareTo(currentNode.next.element) >= 0) {
                    currentNode = currentNode.next;
                }
                newNode.next = currentNode.next;
                currentNode.next = newNode;
                numElements++;
                return true;
            }
        }
        return false;
    }

    public void retain() {

    }

    public void remove() {

    }

    public static void main(String[] args) {
        LinkedRRSet test = new LinkedRRSet();
        test.add(5);
        test.add(1);
        test.add(3);
        test.add(4);
        test.add(6);

        System.out.println(test);
    }
}
