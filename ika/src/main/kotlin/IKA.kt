import io.reactivex.Observable

fun main(args: Array<String>) {

    Observable.just(1)
            .subscribe({ print(it) })
}