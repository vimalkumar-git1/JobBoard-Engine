package com.vimalkumar.careerportal.aggregator;

/**
 * One entry in the multi-company fetch list.
 * atsType must be "greenhouse" or "lever".
 * boardSlug is the company identifier used in that ATS's public API URL —
 * e.g. Greenhouse: boards-api.greenhouse.io/v1/boards/{boardSlug}/jobs
 *      Lever:      api.lever.co/v0/postings/{boardSlug}
 *
 * Find a company's real slug by opening their careers page and checking the
 * network tab / page source for a greenhouse.io or lever.co URL. Never guess —
 * a wrong slug just returns an empty/404 result, so it's safe to try, but it
 * won't pull real data until the correct slug is used.
 */
public record CompanyAtsSource(String companyDisplayName, String atsType, String boardSlug) {
}
