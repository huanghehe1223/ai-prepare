package upc.projectname.knowledgebaseservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import upc.projectname.upccommon.domain.po.KnowledgeBase;
import upc.projectname.knowledgebaseservice.mapper.KnowledgeBaseMapper;
import upc.projectname.knowledgebaseservice.service.KnowledgeBaseService;

import java.util.List;

@Service
public class KnowledgeBaseServiceImpl extends ServiceImpl<KnowledgeBaseMapper, KnowledgeBase> implements KnowledgeBaseService {

    @Override
    public KnowledgeBase getKnowledgeBaseById(Integer id) {
        return this.getById(id);
    }

    @Override
    public boolean saveKnowledgeBase(KnowledgeBase knowledgeBase) {
        return this.save(knowledgeBase);
    }

    @Override
    public boolean updateKnowledgeBase(KnowledgeBase knowledgeBase) {
        return this.updateById(knowledgeBase);
    }

    @Override
    public boolean deleteKnowledgeBase(Integer id) {
        return this.removeById(id);
    }

    @Override
    public List<KnowledgeBase> getKnowledgeBaseByIds(List<Integer> ids) {
        return this.listByIds(ids);
    }
    
    @Override
    public IPage<KnowledgeBase> getKnowledgeBasePage(Page<KnowledgeBase> page, String subject, String phase, String edition) {
        LambdaQueryWrapper<KnowledgeBase> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(subject != null && !subject.isEmpty(), KnowledgeBase::getSubject, subject)
                .eq(phase != null && !phase.isEmpty(), KnowledgeBase::getPhase, phase)
                .eq(edition != null && !edition.isEmpty(), KnowledgeBase::getEdition, edition);
        return this.page(page, wrapper);
    }

    @Override
    public List<String> getSubjectByPhase(String phase) {
        LambdaQueryWrapper<KnowledgeBase> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KnowledgeBase::getPhase, phase);
        return this.list(wrapper).stream().map(KnowledgeBase::getSubject).distinct().toList();
    }

    @Override
    public List<KnowledgeBase> getKnowledgeBaseByPhaseAndSubject(String phase, String subject) {
        LambdaQueryWrapper<KnowledgeBase> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KnowledgeBase::getPhase, phase)
                .eq(KnowledgeBase::getSubject, subject);
        return this.list(wrapper);
    }

    @Override
    public List<String> getEditionByPhaseAndSubject(String phase, String subject) {
        LambdaQueryWrapper<KnowledgeBase> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KnowledgeBase::getPhase, phase)
                .eq(KnowledgeBase::getSubject, subject);
        return this.list(wrapper).stream().map(KnowledgeBase::getEdition).distinct().toList();
    }
}