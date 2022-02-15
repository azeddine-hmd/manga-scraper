import org.jsoup.nodes.Document

fun scrapeImagesLink(document: Document): List<String> {
	val imagesLink = mutableListOf<String>()
	val imgsTag = document.getElementsByTag("img")
	for (imgTag in imgsTag) {
		imagesLink.add(imgTag.attr("src"))
	}

	return imagesLink
}

fun downloadImages(imagesLink: List<String>, options: Options, chapter: UInt ) {
	val path = options.targetDir + "/" + chapter
	shell.run("mkdir -p $path")

	imagesLink.forEachIndexed { index, imgLink ->

		val result = if (options.bandwidthLimit == Options.BANDWIDTH_NO_LIMIT) {
			shell.run("curl $imgLink -o $path/$index.jpg")
		} else {
			shell.run("trickle -s -d ${options.bandwidthLimit} curl $imgLink -o $path/$index.jpg")
		}

		println("$> ${result.details.command}")
		println(result.output())
		println()
	}

}

fun extractNextChapter(url: String): UInt {
	val urlReversed = url.reversed()

	var start = false
	var completed = false
	val chapterReversed = urlReversed.filter {
		if (completed) return@filter false
		if (!start && it.isDigit()) start = true
		if (start && !it.isDigit()) {
			completed = true
			return@filter false
		}

		it.isDigit()
	}

	return chapterReversed.reversed().toUInt()
}
