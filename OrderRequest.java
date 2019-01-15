package day20190111;

/**
 * @author : hao
 * @project : StudySjms
 * @description :
 * @time : 2019/1/11 15:41
 */
public class OrderRequest {

    String userid;

    String goodid;

    String xdsj;


    public OrderRequest(String userid, String goodid, String xdsj) {
        this.userid = userid;
        this.goodid = goodid;
        this.xdsj = xdsj;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getGoodid() {
        return goodid;
    }

    public void setGoodid(String goodid) {
        this.goodid = goodid;
    }

    public String getXdsj() {
        return xdsj;
    }

    public void setXdsj(String xdsj) {
        this.xdsj = xdsj;
    }
}
