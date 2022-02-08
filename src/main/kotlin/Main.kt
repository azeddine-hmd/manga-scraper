
import com.jaredrummler.ktsh.Shell
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import kotlin.system.exitProcess

data class Input(
	var chapter: Int = 1,
	var url: String = "",
	var baseUrl: String = "https://ww5.read-onepiece.com/manga/",
	var targetDir: String = "/home/azeddine/one-piece",
	var bandwidthLimit: Int = 200,
)

fun main(argc: Array<String>) {
	val input: Input = handlingCliArgument(argc.toList()) ?: exitProcess(Shell.Command.Status.COMMAND_FAILED)

	do {
		val doc = Jsoup.connect(input.url).get()

		val imagesLink = scrapeImagesLink(doc)
		downloadImages(input, imagesLink)

		// resolving the next url and chapter
		input.url = doc.select("a[rel=next]").attr("href")
		input.chapter = Regex(""".*-(\d+)\D""").matchEntire(input.url)?.destructured?.component1()?.toInt() ?: break

	} while (input.url.isNotEmpty())

	exitProcess(Shell.Command.Status.SUCCESS)
}

fun handlingCliArgument(argc: List<String>): Input? {
	val input = Input()

	if (argc.isEmpty()) {
		println("usage: ./manga-scraper [options] initial-chapter")
		println("options:")
		println("--base-url")
		println("--target-dir")
		println("--bandwidth-limit")
		return null
	}

	input.chapter = try {
		argc.last().toInt()
	} catch (e: NumberFormatException) {
		System.err.println("error: failed to parse `${argc.last()}` to integer")
		return null
	}

	for (i in 0 until argc.lastIndex) {
		if ( argc[i].matches( Regex("""--base-url=[^\s|^=]+""") ) ) {
			input.baseUrl = argc[i].split("=").last()
		} else if ( argc[i].matches( Regex("""--target-dir=[^\s|^=]+""") ) ) {
			input.targetDir = argc[i].split("=").last()
		} else if ( argc[i].matches( Regex("""--bandwidth-limit=\d+""") ) ) {
			input.bandwidthLimit = argc[i].split("=").last().toInt()
		} else {
			System.err.println("error: unknown option `${argc[i]}`")
			return null
		}
	}

	input.url = input.baseUrl + "one-piece-chapter-" + input.chapter

	return input
}

fun scrapeImagesLink(document: Document): List<String> {
	val imagesLink = mutableListOf<String>()
	val imgsTag = document.getElementsByTag("img")
	for (imgTag in imgsTag) {
		imagesLink.add(imgTag.attr("src"))
	}

	return imagesLink
}

fun downloadImages(input: Input, imagesLink: List<String>) {
	Shell("bash").run("mkdir -p ${input.targetDir}/${input.chapter}")

	imagesLink.forEachIndexed { index, imgLink ->
		val result = Shell("bash").run("trickle -s -d ${input.bandwidthLimit} curl $imgLink -o ${input.targetDir}/${input.chapter}/$index.jpg")

		println("$> ${result.details.command}")
		println(result.output())
		println()
	}
}
