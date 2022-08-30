package com.allendowney.thinkdast;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.StringTokenizer;

/**
 * @author downey
 *
 */
public class WikiParser {

  // The list of paragraphs we should search
  private Elements paragraphs;

  // The stack of open delimiters
  // TODO: consider simplifying this by counting parentheses
  private Deque<String> parenthesisStack;

  /**
   * Initializes a WikiParser with a list of Elements.
   *
   * @param Elements paragraphs
   */
  public WikiParser(Elements paragraphs) {
    this.paragraphs = paragraphs;
    this.parenthesisStack = new ArrayDeque<String>();
  }

  /**
   * Searches the paragraphs for a valid link.
   *
   * Warns if a paragraph ends with unbalanced parentheses.
   *
   * @return
   */
  public Element findFirstLink() {
    for (Element paragraph : paragraphs) {
      Element firstLink = findFirstLinkPara(paragraph);
      if (firstLink != null) {
        return firstLink;
      }
      if (!parenthesisStack.isEmpty()) {
        System.err.println("Warning: unbalanced parentheses.");
      }
    }
    return null;
  }

  /**
   * Returns the first valid link in a paragraph, or null.
   *
   * @param Node root
   */
  private Element findFirstLinkPara(Node root) {
    // create an Iterable that traverses the tree
    Iterable<Node> nt = new WikiNodeIterable(root);

    // loop through the nodes
    for (Node node : nt) {
      // process TextNodes to get parentheses
      if (node instanceof TextNode) {
        processTextNode((TextNode) node);
      }
      // process elements to get find links
      if (node instanceof Element) {
        Element firstLink = processElement((Element) node);
        if (firstLink != null) {
          return firstLink;
        }
      }
    }
    return null;
  }

  /**
   * Returns the element if it is a valid link, null otherwise.
   *
   * @param elt
   */
  private Element processElement(Element elt) {
    if (validLink(elt)) {
      return elt;
    }
    return null;
  }

  /**
   * Checks whether a link is valid.
   *
   * @param elt
   * @return
   */
  private boolean validLink(Element elt) {
    // It's no good if it's not a link
    if (!elt.tagName().equals("a")) {
      return false;
    }
    // in italics
    if (isItalic(elt)) {
      return false;
    }
    // in parenthesis
    if (isInParens(elt)) {
      return false;
    }
    // a bookmark
    if (startsWith(elt, "#")) {
      return false;
    }
    // a Wikipedia help page
    if (startsWith(elt, "/wiki/Help:")) {
      return false;
    }
    // TODO: there are a couple of other "rules" we haven't handled
    return true;
  }

  /**
   * Checks whether a link starts with a given String.
   *
   * @param elt
   * @param s
   * @return
   */
  private boolean startsWith(Element elt, String s) {
    return (elt.attr("href").startsWith(s));
  }

  /**
   * Checks whether the element is in parenthesis (possibly nested).
   *
   * @param elt
   * @return
   */
  private boolean isInParens(Element elt) {
    // check whether there are any parenthesis on the stack
    return !parenthesisStack.isEmpty();
  }

  /**
   * Checks whether the element is in italics.
   *
   * (Either a "i" or "em" tag)
   *
   * @param start
   * @return
   */
  private boolean isItalic(Element start) {
    // Follow the parent chain until we get to null
    for (Element elt = start; elt != null; elt = elt.parent()) {
      if (elt.tagName().equals("i") || elt.tagName().equals("em")) {
        return true;
      }
    }
    return false;
  }

  /**
   * Processes a text node, splitting it up and checking parentheses. It saves
   * open parenthesis
   * in the parenthesisStack, removes the opening parenthesis if it finds closing
   * ones. Only open
   * parenthesis should remain in the stack.
   *
   * @param node
   */
  private void processTextNode(TextNode node) {
    StringTokenizer st = new StringTokenizer(node.text(), " ()", true);
    while (st.hasMoreTokens()) {
      String token = st.nextToken();
      if (token.equals("(")) {
        parenthesisStack.push(token);
      }
      if (token.equals(")")) {
        if (parenthesisStack.isEmpty()) {
          System.err.println("Warning: unbalanced parentheses.");
        }
        parenthesisStack.pop();
      }
    }
  }
}
