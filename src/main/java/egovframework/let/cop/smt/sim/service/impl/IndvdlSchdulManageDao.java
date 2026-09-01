package egovframework.let.cop.smt.sim.service.impl;

import java.util.List;
import java.util.Map;

import org.egovframe.rte.psl.dataaccess.EgovAbstractMapper;
import org.egovframe.rte.psl.dataaccess.util.EgovMap;
import org.springframework.stereotype.Repository;

import egovframework.com.cmm.ComDefaultVO;
import egovframework.let.cop.smt.sim.service.IndvdlSchdulManageVO;

/**
 * 일정관리를 처리하는 Dao Class 구현
 * @since 2009.04.10
 * @see
 * <pre>
 * << 개정이력(Modification Information) >>  수정일      수정자           수정내용 -------    ---
 * -----    --------------------------- 2009.04.10  장동한          최초 생성 2011.05.31
 * JJY           경량환경 커스터마이징버전 생성
 * </pre>
 * @author 조재영
 * @version 1.0
 * @created 09-6-2011 오전 10:08:07
 */
@Repository
public class IndvdlSchdulManageDao extends EgovAbstractMapper {

	/**
	 * 메인페이지/일정관리조회 목록을 Map(map)형식으로 조회한다.
	 * @param Map(map) - 조회할 정보가 담긴 VO
	 * @return List
	 */
	public List<EgovMap> selectIndvdlSchdulManageMainList(Map<String, Object> map) {
		return selectList("IndvdlSchdulManage.selectIndvdlSchdulManageMainList", map);
	}

	/**
	 * 일정 목록을 Map(map)형식으로 조회한다.
	 * @param Map(map) - 조회할 정보가 담긴 VO
	 * @return List
	 */
	public List<EgovMap> selectIndvdlSchdulManageRetrieve(Map<String, Object> map) {
		return selectList("IndvdlSchdulManage.selectIndvdlSchdulManageRetrieve", map);
	}

	/**
	 * 일정 목록을 VO(model)형식으로 조회한다.
	 * @param indvdlSchdulManageVO - 조회할 정보가 담긴 VO
	 * @return IndvdlSchdulManageVO
	 */
	public IndvdlSchdulManageVO selectIndvdlSchdulManageDetailVO(IndvdlSchdulManageVO indvdlSchdulManageVO) {
		return selectOne("IndvdlSchdulManage.selectIndvdlSchdulManageDetailVO",
			indvdlSchdulManageVO);
	}

	/**
	 * 일정 목록을 조회한다.
	 * @param searchVO - 조회할 정보가 담긴 VO
	 * @return List
	 */
	public List<EgovMap> selectIndvdlSchdulManageList(ComDefaultVO searchVO) {
		return selectList("IndvdlSchdulManage.selectIndvdlSchdulManage", searchVO);
	}

	/**
	 * 일정를(을) 상세조회 한다.
	 * @param indvdlSchdulManageVO - 일정 정보 담김 VO
	 * @return List
	 */
	public IndvdlSchdulManageVO selectIndvdlSchdulManageDetail(IndvdlSchdulManageVO indvdlSchdulManageVO) {
		return selectOne("IndvdlSchdulManage.selectIndvdlSchdulManageDetailVO", indvdlSchdulManageVO);
	}

	/**
	 * 일정를(을) 목록 전체 건수를(을) 조회한다.
	 * @param searchVO - 조회할 정보가 담긴 VO
	 * @return int
	 */
	public int selectIndvdlSchdulManageListCnt(ComDefaultVO searchVO) {
		return selectOne("IndvdlSchdulManage.selectIndvdlSchdulManageCnt", searchVO);
	}

	/**
	 * 일정를(을) 등록한다.
	 * @param qindvdlSchdulManageVO - 일정 정보 담김 VO
	 */
	public int insertIndvdlSchdulManage(IndvdlSchdulManageVO indvdlSchdulManageVO) {
		return insert("IndvdlSchdulManage.insertIndvdlSchdulManage", indvdlSchdulManageVO);
	}

	/**
	 * 일정를(을) 수정한다.
	 * @param indvdlSchdulManageVO - 일정 정보 담김 VO
	 */
	public int updateIndvdlSchdulManage(IndvdlSchdulManageVO indvdlSchdulManageVO) {
		return update("IndvdlSchdulManage.updateIndvdlSchdulManage", indvdlSchdulManageVO);
	}

	/**
	 * 일정를(을) 삭제한다.
	 * @param indvdlSchdulManageVO - 일정 정보 담김 VO
	 */
	public int deleteIndvdlSchdulManage(IndvdlSchdulManageVO indvdlSchdulManageVO) {
		// 일지 삭제
		//delete("IndvdlSchdulManage.deleteDiaryManage", indvdlSchdulManageVO);
		// 일정관리 삭제
		return delete("IndvdlSchdulManage.deleteIndvdlSchdulManage", indvdlSchdulManageVO);
	}
}
