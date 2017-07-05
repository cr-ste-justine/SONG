package org.icgc.dcc.song.core.utils;

import lombok.NoArgsConstructor;
import lombok.val;

import java.util.List;
import java.util.stream.Stream;

import static java.lang.Thread.currentThread;
import static lombok.AccessLevel.PRIVATE;
import static org.icgc.dcc.common.core.util.Joiners.NEWLINE;
import static org.icgc.dcc.common.core.util.stream.Collectors.toImmutableList;
import static org.icgc.dcc.common.core.util.stream.Streams.stream;

@NoArgsConstructor(access = PRIVATE)
public class Debug {

  /**
   * Gets the stacktrace List of the calling function
   */
  public static List<StackTraceElement> getCallingStackTrace(){
    return stream(currentThread().getStackTrace())
        .skip(2)
        .collect(toImmutableList());
  }

  public static Stream<StackTraceElement> streamCallingStackTrace(){
    return stream(currentThread().getStackTrace())
        .skip(2);
  }

  public static void sleepMs(long timeMs){
    try {
      Thread.sleep(timeMs);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public static String generateHeader(String title, int lengthOfHeader, String symbol){
    int t = title.length();
    int n = Math.max(t+2, lengthOfHeader);
    int c = symbol.length();
    int leftTerm = c*(n-2);
    String adjustedTitle = title;
    boolean isLeftTermOdd = leftTerm%2==1;
    boolean isRightTermOdd = t%2==1;
    int adjusted_t = t;

    //Odd - Odd = Even, so if title is even, add an extra whitespace so its odd
    if (isLeftTermOdd || isRightTermOdd){
      adjusted_t += 1;
      adjustedTitle += " ";
    }
    int p = (leftTerm - adjusted_t)/2; //numerator is even

    String line = generateChars(symbol, n);
    String pWhiteSpace = generateChars(" ",p);
    String center = symbol+pWhiteSpace+adjustedTitle+pWhiteSpace+symbol;
    return NEWLINE.join(line, center, line);
  }


  public static String generateChars(String c, int num){
    val sb = new StringBuilder(num);
    for (int i =0; i <num; i++){
      sb.append(c);
    }
    return sb.toString();
  }

}