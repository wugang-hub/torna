package cn.torna.common;

import cn.torna.common.util.IdUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author wugang
 */
public class HashidTest {

    @Test
    public void testGen() {
        Long val = 711111231231235L;
        String hash = IdUtil.encode(val);
        System.out.println(hash);

        Long val2 = IdUtil.decode(hash);
        Assert.assertEquals(val, val2);
    }

    @Test
    public void testGen2() {
        Long val = -IdUtil.MAX;
        String hash = IdUtil.encode(val);
        System.out.println(hash);

        Long val2 = IdUtil.decode(hash);
        Assert.assertEquals(val, val2);
    }

}
