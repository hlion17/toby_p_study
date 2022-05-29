package complex.module;

public class CoreServiceImpl implements CoreService {

    private CoreDao coreDao;

    public void setCoreDao(CoreDao coreDao) {
        this.coreDao = coreDao;
    }
}
