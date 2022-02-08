import com.example.demodmesa.datafetchers.ShowsDataFetcher
import com.example.demodmesa.generated.types.Show
import com.jayway.jsonpath.TypeRef
import com.netflix.graphql.dgs.DgsQueryExecutor
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [DgsAutoConfiguration::class, ShowsDataFetcher::class])
private class ShowDataFetcherJunitTest {

	@Autowired
	lateinit var dgsQueryExecutor: DgsQueryExecutor
	lateinit var newShow: Show

	@BeforeEach
	fun createData() {
		newShow = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
			"""mutation{
                        addShow(title:"Nuevo Rico Nuevo Pobre", releaseYear:2021){
                            title,
                            releaseYear
                        }
                    }
                """.trimIndent(),
			"data.addShow",
			object : TypeRef<Show>() {}
		)
	}

	@AfterEach
	fun deleteData() {
		dgsQueryExecutor.execute(
			"""mutation{
                  deleteShow(title:"${newShow.title}")
                }
                """
		)
	}

	@Test
	@DisplayName("Should return all shows mapped as List<String>")
	fun showsAsListOfString() {
		val titles : List<String> = dgsQueryExecutor.executeAndExtractJsonPath("""
            {
                getShows {
                    title
                    releaseYear
                }
            }
        """.trimIndent(), "data.getShows[*].title")
		assertThat(titles.size).isGreaterThan(1)
		assertThat(titles).contains(newShow.title)
	}

	@Test
	@DisplayName("Should return all shows as an object")
	fun showsAsListOfShow() {
		val showList : List<Show> = dgsQueryExecutor.executeAndExtractJsonPathAsObject("""
            {
                getShows {
                    title
                    releaseYear
                }
            }
        """.trimIndent(), "data.getShows[*]",
			object : TypeRef<List<Show>>() {})

		assertThat(showList.size).isGreaterThan(1)
		assertThat(showList.find { it.title == newShow.title }).isEqualTo(newShow)

	}

	@Test
	@DisplayName("Should return specific show when pass a title filter")
	fun showByTitle() {
		val showList : List<Show> = dgsQueryExecutor.executeAndExtractJsonPathAsObject("""
            {
                getShows (titleFilter:"${newShow.title}") {
                    title
                    releaseYear
                }
            }
        """.trimIndent(), "data.getShows",
			object : TypeRef<List<Show>>() {})

		assertThat(showList.size).isEqualTo(1)

		assertThat(showList.first().title).isEqualTo(newShow.title)
	}
}
