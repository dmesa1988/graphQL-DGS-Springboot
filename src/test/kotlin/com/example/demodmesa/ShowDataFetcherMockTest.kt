package com.example.demodmesa

import com.example.demodmesa.datafetchers.ShowsDataFetcher
import com.example.demodmesa.generated.types.Show
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration
import com.ninjasquad.springmockk.SpykBean
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.inspectors.forAtLeastOne
import io.kotest.matchers.shouldBe
import io.kotest.spring.SpringListener
import io.mockk.clearAllMocks
import io.mockk.every
import org.springframework.boot.test.context.SpringBootTest
import kotlin.properties.Delegates

@SpringBootTest(classes = [DgsAutoConfiguration::class, ShowsDataFetcher::class])
class ShowDataFetcherMockTest: DescribeSpec(){

    @SpykBean
    lateinit var showsDataFetcher: ShowsDataFetcher


    override fun listeners() = listOf(SpringListener)

    init{
        var releaseYear_1 by Delegates.notNull<Int>()
        var title_1 by Delegates.notNull<String>()
        var releaseYear_2 by Delegates.notNull<Int>()
        var title_2 by Delegates.notNull<String>()
        lateinit var mockedShow_1:Show
        lateinit var mockedShow_2:Show
        lateinit var mockedShowsList: List<Show>

        beforeEach() {
            title_1 = "Titanic"
            releaseYear_1 = 1998
            mockedShow_1 = Show(title_1, releaseYear_1)
            title_2 = "Djando Unchained"
            releaseYear_2 = 2007
            mockedShow_2 = Show(title_2, releaseYear_2)
            mockedShowsList = listOf(mockedShow_1,mockedShow_2)

            every {
                showsDataFetcher.selectShow(any())
            } returns mockedShowsList

            every {
                showsDataFetcher.insertShow(any())
            } returns mockedShow_1
        }

        afterEach(){
            clearAllMocks()
        }


        describe("Shows") {


            it("Should return all shows as an object") {
                val showList: List<Show> =showsDataFetcher.getShows("")
                showList.forAtLeastOne {
                    it shouldBe mockedShow_1
                }

                showList.forAtLeastOne {
                    it shouldBe mockedShow_2
                }
            }

            it("Should insert new object") {
                val show: Show = showsDataFetcher.addShow(title_1,releaseYear_1)
                show shouldBe mockedShow_1
            }
        }

    }
}
