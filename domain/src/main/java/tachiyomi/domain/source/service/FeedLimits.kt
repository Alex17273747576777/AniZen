package tachiyomi.domain.source.service

// ANZ -->
/**
 * Feed composition limits.
 *
 * AniZen had no cap at all: `InsertFeedSavedSearch.await` inserted unconditionally and every visible
 * category is fetched on refresh (`FeedScreenModel.setupFeedSubscriptions`), so a tab could grow
 * without bound. The unit that matters is the tab, not the whole feed: 25 sources per tab, unlimited
 * tabs (`feed_categories` already models them).
 *
 * MAX_CONCURRENT_FETCHES_PER_TAB keeps a tab refresh bounded no matter how full the tab is.
 */
object FeedLimits {
    const val MAX_SOURCES_PER_TAB: Int = 25 // ANZ
    const val MAX_CONCURRENT_FETCHES_PER_TAB: Int = 6
}
// ANZ <--
