package org.icgc.dcc.song.importer.filters;

import lombok.val;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.icgc.dcc.common.core.util.stream.Collectors.toImmutableList;
import static org.icgc.dcc.common.core.util.stream.Collectors.toImmutableSet;

public interface Filter<T> {

  boolean isPass(T t);

  default boolean isFail(T t){
    return !isPass(t);
  }

  default List<T> passList(Collection<T> collection){
    return passStream(collection).collect(toImmutableList());
  }

  default Set<T> passSet(Collection<T> collection){
    return passStream(collection).collect(toImmutableSet());
  }

  default List<T> failList(Collection<T> collection){
    return failStream(collection).collect(toImmutableList());
  }

  default Set<T> failSet(Collection<T> collection){
    return failStream(collection).collect(toImmutableSet());
  }

  default Stream<T> passStream(Collection<T> collection){
    return collection.stream()
        .filter(this::isPass);
  }

  default Stream<T> failStream(Collection<T> collection){
    return collection.stream()
        .filter(this::isFail);
  }

  static <T> Filter<T> passThrough(){
    return new Filter<T>() {

      @Override public boolean isPass(T t) {
        return true;
      }
    };

  }

  static <T> Filter<T> cascade(Filter<T> ...filters){
    return new Filter<T>() {

      @Override
      public boolean isPass(T t) {
        for (val filter : filters){
          val result = filter.isPass(t);
          if (!result){
            return false;
          }
        }
        return true;
      }
    };
  }


}
