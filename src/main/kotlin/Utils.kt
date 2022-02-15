import com.jaredrummler.ktsh.Shell

val shell = Shell("sh")

fun formatRemotePath(remotePath: String, chapter: UInt) = remotePath.replace(Options.PLACEHOLDER, chapter.toString())
