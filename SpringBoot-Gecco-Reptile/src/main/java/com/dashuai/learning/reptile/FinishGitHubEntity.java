package com.dashuai.learning.reptile;


import com.dashuai.learning.utils.json.JSONParseUtils;
import com.geccocrawler.gecco.annotation.PipelineName;
import com.geccocrawler.gecco.pipeline.Pipeline;

/**
 * Finish git hub entity
 * Created in 2020.08.14
 *
 * @author Liaozihong
 */
@PipelineName("finishPipeline")
public class FinishGitHubEntity implements Pipeline<MyGitHub> {
    @Override
    public void process(MyGitHub bean) {
        System.out.println("结果如下:" + JSONParseUtils.object2JsonString(bean));
    }
}
