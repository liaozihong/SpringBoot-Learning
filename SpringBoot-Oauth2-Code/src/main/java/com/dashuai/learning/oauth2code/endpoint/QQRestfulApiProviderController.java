package com.dashuai.learning.oauth2code.endpoint;

import com.dashuai.learning.oauth2code.model.InMemoryQQDatabase;
import com.dashuai.learning.oauth2code.model.QQAccount;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Qq restful api provider controller
 * <p/>
 * Created in 2019.05.14
 * <p/>
 *
 * @author Liaozihong
 */
@RestController
@RequestMapping("/qq")
public class QQRestfulApiProviderController {

    /**
     * Info qq account.
     *
     * @param qq the qq
     * @return the qq account
     */
    @RequestMapping("/info/{qq}")
    public QQAccount info(@PathVariable("qq") String qq) {
        return InMemoryQQDatabase.database.get(qq);
    }

    /**
     * Fans list.
     *
     * @param qq the qq
     * @return the list
     */
    @RequestMapping("fans/{qq}")
    public List<QQAccount> fans(@PathVariable("qq") String qq) {
        return InMemoryQQDatabase.database.get(qq).getFans();
    }


}
