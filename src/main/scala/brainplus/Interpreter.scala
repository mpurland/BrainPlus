package brainplus

trait Interpreter {
	def run(source: String)(implicit verbose: Boolean)
}