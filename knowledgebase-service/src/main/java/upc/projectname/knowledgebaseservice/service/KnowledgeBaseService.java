package upc.projectname.knowledgebaseservice.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import upc.projectname.upccommon.domain.po.KnowledgeBase;

import java.util.List;

public interface KnowledgeBaseService extends IService<KnowledgeBase> {

    KnowledgeBase getKnowledgeBaseById(Integer id);

    boolean saveKnowledgeBase(KnowledgeBase knowledgeBase);

    boolean updateKnowledgeBase(KnowledgeBase knowledgeBase);

    boolean deleteKnowledgeBase(Integer id);
    
    List<KnowledgeBase> getKnowledgeBaseByIds(List<Integer> ids);
    
    IPage<KnowledgeBase> getKnowledgeBasePage(Page<KnowledgeBase> page, String subject, String phase, String edition);

    List<String> getSubjectByPhase(String phase);

    List<KnowledgeBase> getKnowledgeBaseByPhaseAndSubject(String phase, String subject);

    List<String> getEditionByPhaseAndSubject(String phase, String subject);
}
