package lk.ijse.gdse.springboot.notetakerV2.service;

import lk.ijse.gdse.springboot.notetakerV2.customObj.NoteErrorResponse;
import lk.ijse.gdse.springboot.notetakerV2.customObj.NoteResponse;
import lk.ijse.gdse.springboot.notetakerV2.dao.NoteDao;
import lk.ijse.gdse.springboot.notetakerV2.dto.Impl.NoteDto;
import lk.ijse.gdse.springboot.notetakerV2.entity.NoteEntity;
import lk.ijse.gdse.springboot.notetakerV2.exception.DataPersistFailedException;
import lk.ijse.gdse.springboot.notetakerV2.exception.NoteNotFoundException;
import lk.ijse.gdse.springboot.notetakerV2.util.AppUtil;
import lk.ijse.gdse.springboot.notetakerV2.util.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class NoteServiceImpl implements NoteService {

    @Autowired
    private NoteDao noteDao;
    @Autowired
    private Mapping mapping;

    @Override
    public void saveNote(NoteDto noteDto) {
        noteDto.setNoteId(AppUtil.createNoteId());
        var noteEntity = mapping.convertToEntity(noteDto);
        var savedNote = noteDao.save(noteEntity);
        if ( savedNote == null ){
            throw new DataPersistFailedException("Can't save the note");
        }

    }

    @Override
    public void updateNote(String noteId, NoteDto noteDto) {
        Optional<NoteEntity> tmpNoteEntityById = noteDao.findById(noteId);
        if (!tmpNoteEntityById.isPresent()){
            throw new NoteNotFoundException("Note not found");
        }else {
            tmpNoteEntityById.get().setNoteDesc(noteDto.getNoteDesc());
            tmpNoteEntityById.get().setCreatedDate(noteDto.getCreatedDate());
            tmpNoteEntityById.get().setNoteTitle(noteDto.getNoteTitle());
            tmpNoteEntityById.get().setPriorityLevel(noteDto.getPriorityLevel());
        }
    }

    @Override
    public void deleteNote(String noteId) {
        Optional<NoteEntity> tmpNoteEntityById = noteDao.findById(noteId);
        if (!tmpNoteEntityById.isPresent()){
            throw new NoteNotFoundException("Note not found");
        }else {
            noteDao.deleteById(noteId);
        }
    }

    @Override
    public NoteResponse getNote(String noteId) {
        if (noteDao.existsById(noteId)){
            return mapping.convertToDto(noteDao.getReferenceById(noteId));
        } else {
            return new NoteErrorResponse(0, "Note not found");
        }
    }

    @Override
    public List<NoteDto> getAllNotes() {
        return mapping.convertToDtos(noteDao.findAll());
    }
}