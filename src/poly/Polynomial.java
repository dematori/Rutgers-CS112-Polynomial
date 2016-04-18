package poly;

import java.io.*;
import java.util.StringTokenizer;

/**
 * This class implements a term of a polynomial.
 */
class Term {
	/**
	 * Coefficient of term.
	 */
	public float coeff;

	/**
	 * Degree of term.
	 */
	public int degree;

	/**
	 * Initializes an instance with given coefficient and degree.
	 * 
	 * @param coeff Coefficient
	 * @param degree Degree
	 */
	public Term(float coeff, int degree) {
		this.coeff = coeff;
		this.degree = degree;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object other) {
		return other != null &&
				other instanceof Term &&
				coeff == ((Term)other).coeff &&
				degree == ((Term)other).degree;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		if (degree == 0) {
			return coeff + "";
		} else if (degree == 1) {
			return coeff + "x";
		} else {
			return coeff + "x^" + degree;
		}
	}
}

/**
 * This class implements a linked list node that contains a Term instance.
 * 
 * @author runb-cs112
 *
 */
class Node {

	/**
	 * Term instance. 
	 */
	Term term;

	/**
	 * Next node in linked list. 
	 */
	Node next;

	/**
	 * Initializes this node with a term with given coefficient and degree,
	 * pointing to the given next node.
	 * 
	 * @param coeff Coefficient of term
	 * @param degree Degree of term
	 * @param next Next node
	 */
	public Node(float coeff, int degree, Node next) {
		term = new Term(coeff, degree);
		this.next = next;
	}
}

/**
 * This class implements a polynomial.
 * 
 * @author runb-cs112
 *
 */
public class Polynomial {

	/**
	 * Pointer to the front of the linked list that stores the polynomial. 
	 */ 
	Node poly;

	/** 
	 * Initializes this polynomial to empty, i.e. there are no terms.
	 *
	 */
	public Polynomial() {
		poly = null;
	}

	/**
	 * Reads a polynomial from an input stream (file or keyboard). The storage format
	 * of the polynomial is:
	 * <pre>
	 *     <coeff> <degree>
	 *     <coeff> <degree>
	 *     ...
	 *     <coeff> <degree>
	 * </pre>
	 * with the guarantee that degrees will be in descending order. For example:
	 * <pre>
	 *      4 5
	 *     -2 3
	 *      2 1
	 *      3 0
	 * </pre>
	 * which represents the polynomial:
	 * <pre>
	 *      4*x^5 - 2*x^3 + 2*x + 3 
	 * </pre>
	 * 
	 * @param br BufferedReader from which a polynomial is to be read
	 * @throws IOException If there is any input error in reading the polynomial
	 */
	public Polynomial(BufferedReader br) throws IOException {
		String line;
		StringTokenizer tokenizer;
		float coeff;
		int degree;

		poly = null;

		while ((line = br.readLine()) != null) {
			tokenizer = new StringTokenizer(line);
			coeff = Float.parseFloat(tokenizer.nextToken());
			degree = Integer.parseInt(tokenizer.nextToken());
			poly = new Node(coeff, degree, poly);
		}
	}


	/**
	 * Returns the polynomial obtained by adding the given polynomial p
	 * to this polynomial - DOES NOT change this polynomial
	 * 
	 * @param p Polynomial to be added
	 * @return A new polynomial which is the sum of this polynomial and p.
	 */
	public Polynomial add(Polynomial p) {
		Polynomial retPoly = new Polynomial();
		Polynomial tempPoly = new Polynomial();
		Node currentp1 = this.poly;
		Node currentp2 = p.poly;
		retPoly.poly = null;
		while(currentp1 != null || currentp2 != null){
			Node temp = null;
			if(currentp1 == null){
				temp = new Node(currentp2.term.coeff, currentp2.term.degree, tempPoly.poly);
				currentp2 = currentp2.next;
			}else if(currentp2 == null){	
				temp = new Node(currentp1.term.coeff,currentp1.term.degree, tempPoly.poly);
				currentp1 = currentp1.next;
			}else if(currentp1.term.degree > currentp2.term.degree){
				temp = new Node(currentp2.term.coeff, currentp2.term.degree, tempPoly.poly);
				currentp2 = currentp2.next;
			}else if(currentp2.term.degree > currentp1.term.degree){
				temp = new Node(currentp1.term.coeff, currentp1.term.degree, tempPoly.poly);
				currentp1 = currentp1.next;
			}else{
				float tempCoeff = currentp1.term.coeff + currentp2.term.coeff;
				int tempDegree = currentp1.term.degree;
				currentp1 = currentp1.next;
				currentp2 = currentp2.next;
				if(tempCoeff == 0){
					continue;
				}
				temp = new Node(tempCoeff, tempDegree, tempPoly.poly);
			}
			tempPoly.poly = temp;
			retPoly.poly = tempPoly.poly;
		}
		retPoly = reverse(retPoly);
		return retPoly;
	}

	/**
	 * Returns the polynomial obtained by multiplying the given polynomial p
	 * with this polynomial - DOES NOT change this polynomial
	 * 
	 * @param p Polynomial with which this polynomial is to be multiplied
	 * @return A new polynomial which is the product of this polynomial and p.
	 */
	public Polynomial multiply(Polynomial p) {
		Polynomial retPoly = new Polynomial();
		retPoly.poly = null;
		if(this.poly == null || p.poly == null){
			return retPoly;
		}
		for(Node currentp1 = this.poly; currentp1 != null; currentp1 = currentp1.next){
			for(Node currentp2 = p.poly; currentp2 != null; currentp2 = currentp2.next){
				Polynomial tempPoly = new Polynomial();
				float tempCoeff = currentp1.term.coeff * currentp2.term.coeff;
				int tempDegree = currentp1.term.degree + currentp2.term.degree;
				Node temp = new Node(tempCoeff, tempDegree, tempPoly.poly);
				tempPoly.poly = temp;
				retPoly = retPoly.add(tempPoly);
			}
		}
		return retPoly;
	}

	/**
	 * Evaluates this polynomial at the given value of x
	 * 
	 * @param x Value at which this polynomial is to be evaluated
	 * @return Value of this polynomial at x
	 */
	public float evaluate(float x) {
		float total = 0;
		for(Node current = this.poly; current != null; current = current.next){
			total += current.term.coeff * Math.pow(x, current.term.degree);
		}
		return total;
	}

	/**
	 * Reverses order of polynomial to adhere to the representation of data.
	 * @param p Polynomial that is to be reversed
	 * @return A new polynomial that is the reverse of p
	 */
	private Polynomial reverse(Polynomial p){
		Polynomial retPoly = new Polynomial();
		Node head = null;
		Node tail;
		Node current = p.poly;
		while(current!=null){
			tail = current.next;
			current.next = head;
			head = current;
			current = tail;
		}
		retPoly.poly = head;
		return retPoly;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String retval;

		if (poly == null) {
			return "0";
		} else {
			retval = poly.term.toString();
			for (Node current = poly.next ;
					current != null ;
					current = current.next) {
				retval = current.term.toString() + " + " + retval;
			}
			return retval;
		}
	}
}
