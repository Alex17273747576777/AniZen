package tachiyomi.domain.source.interactor

import logcat.LogPriority
import logcat.asLog
import tachiyomi.core.common.util.system.logcat
import tachiyomi.domain.source.model.FeedSavedSearch
import tachiyomi.domain.source.repository.FeedSavedSearchRepository
import tachiyomi.domain.source.service.FeedLimits

class InsertFeedSavedSearch(
    val feedSavedSearchRepository: FeedSavedSearchRepository,
) {

    suspend fun await(feedSavedSearch: FeedSavedSearch): Long? {
        // ANZ -->
        // Capped here rather than at each call site because three of them insert into a tab
        // (SourcesScreenModel.addToFeed, FeedManageScreenModel.duplicate and
        // FeedManageScreenModel.updateFeedCategory) and a per-call-site guard missed one of them.
        // Returns null on refusal, which is what await already returns on failure, so existing
        // callers keep compiling and keep treating it as "not inserted".
        if (feedSavedSearch.global) {
            val inTab = feedSavedSearchRepository.getGlobal(feedSavedSearch.category)
            if (inTab.size >= FeedLimits.MAX_SOURCES_PER_TAB) { // ANZ
                logcat(LogPriority.WARN) {
                    "Feed tab ${feedSavedSearch.category} is full " +
                        "(max ${FeedLimits.MAX_SOURCES_PER_TAB}); refusing entry for source " +
                        "${feedSavedSearch.source}"
                }
                return null
            }
        }
        // ANZ <--
        return try {
            feedSavedSearchRepository.insert(feedSavedSearch)
        } catch (e: Exception) {
            logcat(LogPriority.ERROR) { e.asLog() }
            null
        }
    }

    // ANZ: deliberately NOT capped — awaitAll is the bulk/restore path, and refusing rows there
    // would silently drop entries from a backup instead of refusing one user action.
    suspend fun awaitAll(feedSavedSearch: List<FeedSavedSearch>) {
        try {
            feedSavedSearchRepository.insertAll(feedSavedSearch)
        } catch (e: Exception) {
            logcat(LogPriority.ERROR) { e.asLog() }
        }
    }
}
