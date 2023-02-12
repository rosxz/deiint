package pt.ulisboa.tecnico.rnl.dei.deiint.main.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.rnl.dei.deiint.exceptions.DeiintException;
import pt.ulisboa.tecnico.rnl.dei.deiint.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.rnl.dei.deiint.main.dto.InterviewDto;
import pt.ulisboa.tecnico.rnl.dei.deiint.main.entity.Interview;
import pt.ulisboa.tecnico.rnl.dei.deiint.main.repository.InterviewRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class InterviewService {
	@Autowired
	private InterviewRepository interviewRepository;

	private Interview fetchInterviewOrThrow(long id) {
		return interviewRepository.findById(id)
				.orElseThrow(() -> new DeiintException(ErrorMessage.NO_SUCH_INTERVIEW, Long.toString(id)));
	}

	public List<InterviewDto> getAllInterviews() {
		return interviewRepository.findAll().stream()
				.map(InterviewDto::new)
				.collect(Collectors.toList());
	}

	public InterviewDto createInterview(InterviewDto interviewDto) {
		// Verify there isn't an interview with the same call name and candidate name, otherwise throw exception
		// To this end, use a find all method // TODO: THIS COULD NOT WORK, TEST
		List<Interview> interviews = interviewRepository.findAll().stream().toList();
		for (Interview interview : interviews) {
            if (interview.getCallId() == interviewDto.getCallId() && interview.getCandidateId() == interviewDto.getCandidateId()) {
                throw new DeiintException(ErrorMessage.DUPLICATE_INTERVIEW, interview.getCandidateId() + " " + interview.getCallId());
            }
        }

		Interview interview = new Interview(interviewDto);

		interview.setId(null); // to ensure that the id is generated by the database
		return new InterviewDto(interviewRepository.save(interview));
	}

	public InterviewDto getInterview(long id) {
		return new InterviewDto(fetchInterviewOrThrow(id));
	}

	public InterviewDto updateInterview(long id, InterviewDto interviewDto) {
		fetchInterviewOrThrow(id); // ensure exists

		// FIXME: check original
		Interview interview = new Interview(interviewDto);
		// TODO: validation, maybe?
		interview.setId(id);
		return new InterviewDto(interviewRepository.save(interview));
	}

	public void deleteInterview(long id) {
		fetchInterviewOrThrow(id); // ensure exists

		interviewRepository.deleteById(id);
	}
}