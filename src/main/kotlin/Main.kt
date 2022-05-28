import com.jaredrummler.ktsh.Shell.Command.Status
import org.jsoup.Jsoup
import kotlin.system.exitProcess

fun main(argc: Array<String>) {

	val options = Options.optionsFromCLA(argc).getOrElse {
		println(it.message)
		exitProcess(1)
	}
	println("options: $options")

	var currentChapter = options.start
	var url = options.baseUrl + formatRemotePath(options.remotePath, currentChapter)
	println("url = $url")
	do {
		val doc = Jsoup.connect(url).get()
		val imagesLink = scrapeImagesLink(doc)
		downloadImages(imagesLink, options, currentChapter)
		url = doc.select("a[rel=next]").attr("href")
		if (url.isEmpty()) break
		currentChapter = extractNextChapter(url)
	} while (currentChapter <= options.end)
	exitProcess(Status.SUCCESS)
}