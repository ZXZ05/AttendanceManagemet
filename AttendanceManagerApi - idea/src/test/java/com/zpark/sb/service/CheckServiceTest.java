package com.zpark.sb.service;

import com.zpark.sb.dao.CheckDao;
import com.zpark.sb.entity.Check;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CheckServiceTest {

    @Mock
    private CheckDao checkDao;

    @InjectMocks
    private CheckService checkService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(checkService, "onTime", "08:30");
        ReflectionTestUtils.setField(checkService, "offTime", "17:30");
    }

    @Test
    void checkOffShouldInsertWhenNoCheckOnRecord() throws Exception {
        Check request = new Check();
        request.setEmployeeID("E001");
        request.setCheckOffTime(new Date());
        when(checkDao.findByNumberAndDate(any(Check.class))).thenReturn(null, null);

        int result = checkService.checkOff(request);

        assertEquals(0, result);
        verify(checkDao).insert(any(Check.class));
        verify(checkDao, never()).update(any(Check.class));
    }

    @Test
    void checkOnShouldFallbackWhenConfigIsInvalid() throws Exception {
        ReflectionTestUtils.setField(checkService, "onTime", "bad");
        Check request = new Check();
        request.setEmployeeID("E002");
        request.setCheckOnTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2026-05-23 08:00:00"));

        checkService.checkOn(request);

        ArgumentCaptor<Check> captor = ArgumentCaptor.forClass(Check.class);
        verify(checkDao).insert(captor.capture());
        Check saved = captor.getValue();
        assertEquals("正常", saved.getCheckOnStatus());
    }
}
