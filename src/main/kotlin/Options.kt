data class Options(
	val start: UInt,
	val end: UInt,
	val baseUrl: String,
	val remotePath: String,
	val targetDir: String,
	val bandwidthLimit: Int,
) {
	companion object {
		const val DEFAULT_END = UInt.MAX_VALUE
		const val DEFAULT_BASE_URL = "https://ww5.read-onepiece.com"
		const val DEFAULT_REMOTE_PATH = "/manga/one-piece-chapter-%d/"
		const val DEFAULT_TARGET_DIR = "/tmp/manga-scraper"
		const val BANDWIDTH_NO_LIMIT = -1
		const val PLACEHOLDER = "%d"

		fun optionsFromCLA(argc: Array<String>): Result<Options> {
			var baseUrl = DEFAULT_BASE_URL
			var remotePath = DEFAULT_REMOTE_PATH
			var targetDir = DEFAULT_TARGET_DIR
			var bandwidthLimit = BANDWIDTH_NO_LIMIT

			if (argc.isEmpty()) return Result.failure(Throwable(usage()))

			val (start, end) = parseChapterRange(argc.last()).getOrElse {
				return Result.failure(it)
			}

			for (i in 0 until argc.lastIndex) {

				if (Regex("""--base-url=[^\s|^=]+""").matches(argc[i])) {
					// Option: --base-url
					baseUrl = argc[i].split("=").last()

				} else if (validPlaceholder(argc[i]) && Regex("""--remote-path=[^\s|^=]+""").matches(argc[i])) {
					// Option: --remote-path
					remotePath = argc[i].split("=").last()

				} else if (Regex("""--target-dir=[^\s|^=]+""").matches(argc[i])) {
					// Option: --target-dir
					targetDir = argc[i].split("=").last()

				} else if (Regex("""--bandwidth-limit=\d+|no""") matches (argc[i])) {
					// Option: --bandwidth-limit
					val limit: String = argc[i].split("=").last()
					bandwidthLimit = if (limit == "no") BANDWIDTH_NO_LIMIT else limit.toInt()

				} else {
					return Result.failure(Throwable(usage()))
				}

			}

			return Result.success(
				Options(
					start, end, baseUrl, remotePath, targetDir, bandwidthLimit
				)
			)
		}

		private fun parseChapterRange(input: String): Result<Pair<UInt, UInt>> {
			val pattern = Regex("""(\d+)-(\d+|max)""")
			val matchResult: MatchResult = pattern.matchEntire(input) ?: return Result.failure(Throwable())
			val (_, firstGroup, secondGroup) = matchResult.groupValues
			val start = firstGroup.toUInt()
			val end = if (secondGroup == "max") DEFAULT_END else secondGroup.toUInt()

			return Result.success(Pair(start, end))
		}

		private fun validPlaceholder(str: String): Boolean {
			if (str.contains(PLACEHOLDER)) {
				val afterPlaceholder = str.substringAfter(PLACEHOLDER)
				val haveSecondPlaceholder = afterPlaceholder.contains(PLACEHOLDER)
				if (haveSecondPlaceholder) {
					return false
				}
			} else {
				return false
			}

			return true
		}


	}
}