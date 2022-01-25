package com.example.demodmesa

import com.example.demodmesa.datafetchers.ShowsDataFetcher
import com.netflix.graphql.dgs.DgsQueryExecutor
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.ints.shouldBeGreaterThan
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [DgsAutoConfiguration::class, ShowsDataFetcher::class])
private class KotestTestJS: DescribeSpec(){
    @Autowired
    lateinit var dgsQueryExecutor: DgsQueryExecutor
    init{
        describe("Shows") {
            it("") {
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
                titles shouldContain "Ozark5"
            }

        }
    }
}
