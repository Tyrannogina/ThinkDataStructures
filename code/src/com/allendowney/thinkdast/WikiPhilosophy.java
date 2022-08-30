package com.allendowney.thinkdast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WikiPhilosophy {

  final static List<String> visitedURLs = new ArrayList<String>();
  final static WikiFetcher wikiFetcher = new WikiFetcher();

  /**
   * Tests a conjecture about Wikipedia and Philosophy.
   *
   * https://en.wikipedia.org/wiki/Wikipedia:Getting_to_Philosophy
   *
   * 1. Clicking on the first non-parenthesized, non-italicized link
   * 2. Ignoring external links, links to the current page, or red links
   * 3. Stopping when reaching "Philosophy", a page with no links or a page
   * that does not exist, or when a loop occurs
   *
   * @param args
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {
    String destination = "https://en.wikipedia.org/wiki/Philosophy";
    // String source = "https://en.wikipedia.org/wiki/Java_(programming_language)"; // 9 links
    String source = "https://en.wikipedia.org/wiki/Potato"; // 22 links

    testConjecture(destination, source, 23);
  }

  /**
   * Starts from given Wikipedia URL and follows first link until it finds the
   * destination or
   * exceeds the limit.
   *
   * @param destination the URL we want to get to
   * @param source      the URL we start from
   * @param limit       times the first url is followed from the source url. Needs to be at least
   * number of links followed + 1, since we only check at the start of the loop.
   * @throws IOException
   */
  public static void testConjecture(String destination, String source, int limit) throws IOException {
    String url = source;
    for (int i = 0; i < limit; i++) {
      if (url.equals(destination)) {
        System.out.println(String.format("We got to Philosophy after following %d links!", i));
        return;
      }

      Element link = getFirstLink(url);
      url = link.attr("abs:href");
      System.out.println(link.text());

      if (visitedURLs.contains(url)) {
        System.out.println("This is a loop. Exiting");
        return;
      } else {
        visitedURLs.add(url);
      }
    }
  }

  /**
   * Gets the first link from a Wikipedia article that is not in parenthesis,
   * italics, etc.
   *
   * @param url
   * @return Element
   */
  private static Element getFirstLink(String url) throws IOException {
    Elements paragraphs = wikiFetcher.fetchWikipedia(url);
    WikiParser wp = new WikiParser(paragraphs);
    return wp.findFirstLink();
  }
}
