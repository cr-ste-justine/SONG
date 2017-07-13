/*
 * Copyright (c) 2016 The Ontario Institute for Cancer Research. All rights reserved.
 *
 * This program and the accompanying materials are made available under the terms of the GNU Public License v3.0.
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.icgc.dcc.song.server.importer.convert;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import static lombok.AccessLevel.PRIVATE;
import static org.icgc.dcc.song.server.importer.convert.FieldNames.GENDER;
import static org.icgc.dcc.song.server.importer.convert.FieldNames.ID;
import static org.icgc.dcc.song.server.importer.convert.FieldNames.PROJECT_NAME;
import static org.icgc.dcc.song.server.importer.convert.FieldNames.SPECIMEN;

@NoArgsConstructor(access = PRIVATE)
public final class PortalDonorMetadataParser {

  public static String getProjectName(@NonNull JsonNode donor){
    return donor.path(PROJECT_NAME).textValue();
  }

  public static String getDonorId(@NonNull JsonNode donor){
    return donor.path(ID).textValue();
  }

  public static String getGender(@NonNull JsonNode donor){
    return donor.path(GENDER).textValue();
  }

  public static int getNumSpecimens(@NonNull JsonNode donor){
    return donor.path(SPECIMEN).size();
  }


  public static JsonNode getSpecimen(@NonNull JsonNode donor, int specimenIdx){
    return donor.path(SPECIMEN).get(specimenIdx);
  }

}
