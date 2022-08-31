package com.allendowney.thinkdast;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import org.junit.Before;
import org.junit.Test;

/**
 * @author downey
 */
public class MyHashMapTest extends MyLinearMapTest {

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    map = new MyHashMap<String, Integer>();
    map.put("One", 1);
    map.put("Two", 2);
    map.put("Three", 3);
    map.put(null, 0);
  }

  /**
   * Test method for {@link MyHashMap#rehash(java.lang.Object)}.
   */
  @Test
  public void testRehash() {
    MyHashMap<String, Integer> hashMap = new MyHashMap<String, Integer>();
    System.out.println(hashMap);
    assertThat(hashMap.getMaps().size(), is(2));

    // After adding values, we check the resizing happened.
    for (int i = 0; i < 10; i++) {
      hashMap.put(new Integer(i).toString(), i);
    }
    assertThat(hashMap.getMaps().size(), is(16));
  }
}
