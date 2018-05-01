/*
 * Copyright (c) 2018. Ontario Institute for Cancer Research
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.icgc.dcc.song.server.repository.search;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.hibernate.Session;

import javax.persistence.EntityManager;
import java.util.List;

import static org.icgc.dcc.common.core.util.stream.Collectors.toImmutableList;
import static org.icgc.dcc.song.core.utils.JsonUtils.readTree;
import static org.icgc.dcc.song.server.repository.search.InfoSearchResponse.createWithInfo;
import static org.icgc.dcc.song.server.repository.search.SearchQueryBuilder.createSearchQueryBuilder;

@RequiredArgsConstructor
public class SearchRepository {

  private final EntityManager em;

  public List<InfoSearchResponse> infoSearch(boolean includeInfo, @NonNull Iterable<SearchTerm> searchTerms){
    val session = em.unwrap(Session.class);
    val searchQueryBuilder = createSearchQueryBuilder(includeInfo);
    searchTerms.forEach(searchQueryBuilder::add);

    Object output = session.createNativeQuery(searchQueryBuilder.build()).getResultList();
    if (includeInfo){
      return ((List<Object[]>)output).stream()
          .map(SearchRepository::mapWithInfo)
          .collect(toImmutableList());
    } else {
      return ((List<String>)output).stream()
          .map(InfoSearchResponse::createWithoutInfo)
          .collect(toImmutableList());
    }
  }

  @SneakyThrows
  private static InfoSearchResponse mapWithInfo(Object[] results){
    return createWithInfo(extractAnalysisId(results), readTree(extractInfo(results)));
  }

  private static String extractAnalysisId(Object[] result){
    return (String)result[0];
  }

  private static String extractInfo(Object[] result){
    return (String)result[1];
  }

}
