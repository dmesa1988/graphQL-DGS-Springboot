package com.example.demodmesa

import com.example.demodmesa.datafetchers.ShowsDataFetcher
import com.example.demodmesa.generated.types.Show
import com.jayway.jsonpath.TypeRef
import com.netflix.graphql.dgs.DgsQueryExecutor
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration
import com.ninjasquad.springmockk.SpykBean
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.inspectors.forAtLeastOne
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.spring.SpringListener
import io.mockk.clearAllMocks
import io.mockk.every
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.properties.Delegates

@SpringBootTest(classes = [DgsAutoConfiguration::class, ShowsDataFetcher::class])
class ShowDataFetcherIntegration: DescribeSpec(){

    @Autowired
    lateinit var dgsQueryExecutor: DgsQueryExecutor

    @SpykBean
    lateinit var showsDataFetcher: ShowsDataFetcher


    override fun listeners() = listOf(SpringListener)

    init{
        var releaseYear by Delegates.notNull<Int>()
        var title by Delegates.notNull<String>()
        lateinit var mockedShow:Show

        beforeEach() {
            title = "titanic"
            releaseYear = 1998
            mockedShow = Show(title, releaseYear)
            every {
                showsDataFetcher.selectShow(any())
            } returns listOf(mockedShow)

            every {
                showsDataFetcher.insertShow(any())
            } returns mockedShow
        }

        afterEach(){
            clearAllMocks()
        }


        describe("Shows") {

            it("Should return specific show when pass a title filter") {
                val showList: List<Show> = dgsQueryExecutor.executeAndExtractJsonPathAsObject("""
                    {
                        getShows (titleFilter: "$title") {
                            title
                            releaseYear
                        }
                    }
                    """.trimIndent(), "data.getShows",
                    object : TypeRef<List<Show>>() {})

                showList.size shouldBeExactly 1
                showList.first() shouldBe mockedShow
            }

            it("Should return all shows mapped as List<String>") {
                val showList: List<String> = dgsQueryExecutor.executeAndExtractJsonPath(
                    """
                {
                    getShows {
                        title
                        releaseYear
                    }
                }
                """.trimIndent(), "data.getShows[*].title"
                )

                showList.size shouldBeGreaterThan 0
                showList shouldContain title
            }

            it("Should return all shows as an object") {
                val showList: List<Show> = dgsQueryExecutor.executeAndExtractJsonPathAsObject("""
                {
                    getShows {
                        title
                        releaseYear
                    }
                }
                """.trimIndent(), "data.getShows",
                    object : TypeRef<List<Show>>() {})
                showList.forAtLeastOne {
                    it shouldBe mockedShow
                }
            }

            it("Should insert new object") {
                val show: Show = dgsQueryExecutor.executeAndExtractJsonPathAsObject("""
                mutation {
                    addShow(title: "$title", releaseYear:$releaseYear) {
                        title
                        releaseYear
                    }
                }
                """.trimIndent(), "data.addShow",
                    object : TypeRef<Show>() {})
                show shouldBe mockedShow
            }
        }

    }
}
