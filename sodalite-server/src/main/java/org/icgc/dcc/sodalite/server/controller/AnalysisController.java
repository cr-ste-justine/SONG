/*
 * Copyright (c) 2017 The Ontario Institute for Cancer Research. All rights reserved.
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
package org.icgc.dcc.sodalite.server.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;

import java.util.Arrays;
import java.util.List;

import javax.validation.Valid;

import org.icgc.dcc.sodalite.server.model.Study;
import org.icgc.dcc.sodalite.server.model.SubmissionStatus;
import org.icgc.dcc.sodalite.server.service.RegistrationService;
import org.icgc.dcc.sodalite.server.service.StatusService;
import org.icgc.dcc.sodalite.server.service.StudyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(path = "/studies")
@RequiredArgsConstructor
public class AnalysisController {

  /**
   * Dependencies
   */
  @Autowired
  private final StudyService studyService;
  @Autowired
  private final RegistrationService registrationService;
  @Autowired
  private final StatusService statusService;

  @GetMapping("/{studyId}")
  public List<Study> getStudy(@PathVariable("studyId") String studyId) {
    return Arrays.asList(studyService.getStudy(studyId));
  }

  @GetMapping("/{studyId}/all")
  public Study getEntireStudy(@PathVariable("studyId") String studyId) {
    return studyService.getEntireStudy(studyId);
  }

  @PreAuthorize("@studySecurity.authorize(authentication, #studyId)")
  @PostMapping(value = "/{studyId}/analyses/sequencingread/{uploadId}", consumes = { APPLICATION_JSON_VALUE, APPLICATION_JSON_UTF8_VALUE })
  public ResponseEntity<String> registerSequencingRead(
      @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) final String accessToken,
      @PathVariable("studyId") String studyId,
      @PathVariable("uploadId") String uploadId,
      @RequestBody @Valid String payload) {

    return register("registerSequencingRead", studyId, uploadId, payload, accessToken);
  }

  @PreAuthorize("@studySecurity.authorize(authentication, #studyId)")
  @PostMapping(value = "/{studyId}/analyses/variantcall/{uploadId}", consumes = { APPLICATION_JSON_VALUE, APPLICATION_JSON_UTF8_VALUE })
  public ResponseEntity<String> registerVariantCall(
      @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) final String accessToken,
      @PathVariable("studyId") String studyId,
      @PathVariable("uploadId") String uploadId,
      @RequestBody @Valid String payload) {
    return register("registerVariantCall", studyId, uploadId, payload, accessToken);
  }

  /**
   * Common registration logic for both Sequencing Reads and Variant Calls
   * 
   * @param schemaName
   * @param studyId
   * @param uploadId
   * @param payload
   * @return
   */
  protected ResponseEntity<String> register(String schemaName, String studyId, String uploadId, String payload,
      final String accessToken) {

    // do pre-check for whether this upload id has been used. We want to return
    // this error synchronously
    if (statusService.exists(studyId, uploadId)) {
      return conflict(studyId, uploadId);
    }

    try {
      registrationService.register(schemaName, studyId, uploadId, payload, accessToken);
    } catch (Exception e) {
      log.error(e.toString());
      return badRequest().body(e.getMessage());
    }
    return ok(uploadId);
  }

  @PostMapping(value = "/{studyId}/", consumes = { APPLICATION_JSON_VALUE, APPLICATION_JSON_UTF8_VALUE })
  @ResponseBody

  @PreAuthorize("@studySecurity.authorize(authentication, #studyId)")
  public int saveStudy(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) final String accessToken,
      @RequestBody Study study) {
    return studyService.saveStudy(study);
  }

  @GetMapping(value = "/{studyId}/statuses/{uploadId}")
  public ResponseEntity<SubmissionStatus> getStatus(
      @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) final String accessToken,
      @PathVariable("studyId") String studyId,
      @PathVariable("uploadId") String uploadId) {

    val status = statusService.getStatus(studyId, uploadId);

    if (status == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return ok(status);
  }

  protected ResponseEntity<String> conflict(String studyId, String uploadId) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(String.format("The upload id '%s' has already been used in a previous submission for this study (%s)",
            uploadId, studyId));
  }

  protected ResponseEntity<String> notFound(String studyId, String uploadId) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(String.format("The specified id '%s' was not found for this study (%s)", uploadId, studyId));
  }
}
