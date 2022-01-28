package com.example.demodmesa

import com.example.demodmesa.datafetchers.ShowsDataFetcher
import com.example.demodmesa.types.Show
import com.jayway.jsonpath.TypeRef
import com.netflix.graphql.dgs.DgsQueryExecutor
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.inspectors.forAtLeastOne
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.spring.SpringListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [DgsAutoConfiguration::class, ShowsDataFetcher::class])
class ShowDataFetcherKotestTest: DescribeSpec(){

    @Autowired
    lateinit var dgsQueryExecutor: DgsQueryExecutor

    override fun listeners() = listOf(SpringListener)

    init{
        lateinit var newShow: Show

        beforeEach() {

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

        afterEach() {

            dgsQueryExecutor.execute(
                """mutation{
                  deleteShow(title:"${newShow.title}")
                }
                """
            )
        }


        describe("Shows") {
            it("Should return all shows mapped as List<String>") {
                val titles : List<String> = dgsQueryExecutor.executeAndExtractJsonPath(
                    """
                    {
                        getShows {
                            title
                            releaseYear
                        }
                    }
                """.trimIndent(), "data.getShows[*].title")
                titles.size shouldBeGreaterThan 0
                titles shouldContain newShow.title
            }


            it("Should return all shows as an object") {
                val showList : List<Show> = dgsQueryExecutor.executeAndExtractJsonPathAsObject("""
                {
                    getShows {
                        title
                        releaseYear
                    }
                }
                """.trimIndent(), "data.getShows[*]",
                    object : TypeRef<List<Show>>() {})

                showList.size shouldBeGreaterThan 1
                showList.forAtLeastOne {
                    it.title shouldBe newShow.title
                    it.releaseYear shouldBe newShow.releaseYear
                }
            }

            it("Should return specific show when pass a title filter") {
                val showList : List<Show> = dgsQueryExecutor.executeAndExtractJsonPathAsObject("""
                {
                    getShows (titleFilter: "${newShow.title}") {
                        title
                        releaseYear
                    }
                }
                """.trimIndent(), "data.getShows",
                    object : TypeRef<List<Show>>() {})

                showList.size shouldBeExactly 1
                showList.first() shouldBe newShow

            }

        }

    }
}
