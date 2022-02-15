fun usage(): String =
	"""
		./manga-scraper [OPTIONS] START-END
		DESCRIPTION:
		manga scraper handles scraping and extracting images from desired website and follow links until final chapter 
		reached and download them into local machine.
		note: intended for manga websites
		
		START:              chapter number to start from
		END:                chapter number to stop at
		OPTIONS:
		--base-url:         website url for example (e.g "https://example.com")
		--remote-path:      remote path with number placeholder %d reserved for chapter number (e.g "/manga/chapter-%d")
		--target-dir:       absolute path to save scraped images
		--bandwidth-limit:  limit download speed (x kb/s) (x is number or 'no' for unlimited speed)
							note: limiting needs 'trickle' package to be present
							
		DEFAULTS:
		chapter range: 1-max
		--base-url=https://ww5.read-onepiece.com
		--remote-path=/manga/one-piece-chapter-%d
		--target-dir=/tmp/manga-scraper
		--bandwidth-limit=no
	""".trimIndent()
