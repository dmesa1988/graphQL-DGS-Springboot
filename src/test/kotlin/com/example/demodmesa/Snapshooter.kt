package com.example.demodmesa

import com.example.demodmesa.datafetchers.ShowsDataFetcher
import com.example.demodmesa.types.Show
import com.jayway.jsonpath.TypeRef
import com.netflix.graphql.dgs.DgsQueryExecutor
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [DgsAutoConfiguration::class, ShowsDataFetcher::class])
private class SnapshooterTest {

    @Autowired
    lateinit var dgsQueryExecutor: DgsQueryExecutor

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
        assertThat(titles).contains("Ozark")
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
        assertThat(showList.find { it.title == "Ozark" }).isEqualTo(Show(title="Ozark", releaseYear=2017))

    }

    @Test
    @DisplayName("Should return specific show when pass a title filter")
    fun showByTitle() {
        val showList : List<Show> = dgsQueryExecutor.executeAndExtractJsonPathAsObject("""
            {
                getShows (titleFilter:"Ozark") {
                    title
                    releaseYear
                }
            }
        """.trimIndent(), "data.getShows",
            object : TypeRef<List<Show>>() {})

        assertThat(showList.size).isEqualTo(1)

        assertThat(showList.first().title).isEqualTo("Ozark")
    }
}
