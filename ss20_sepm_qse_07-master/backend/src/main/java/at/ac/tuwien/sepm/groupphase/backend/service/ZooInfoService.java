package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.ZooInfo;

public interface ZooInfoService {

    /**
     * finds an single zooInfo.
     * @param id of animal to be deleted.
     * @return
     */
    ZooInfo findZooInfoById(Long id);

    /**
     * Editing Animal that is already in the Database
     *
     * @param zooInfo to be edited
     * @return edited ZooInfo as saved in the Database
     */
    ZooInfo editZooInfo(ZooInfo zooInfo);

    /**
     * Displaying Zoo information
     * @return zoo info
     */
    ZooInfo displayZooInfo();


}
