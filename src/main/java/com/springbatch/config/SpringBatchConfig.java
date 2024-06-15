package com.springbatch.config;

import com.springbatch.entity.TicketBooking;
import com.springbatch.repository.TicketBookingRepository;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Configuration
@EnableBatchProcessing
@AllArgsConstructor
public class SpringBatchConfig {

    private JobBuilderFactory jobBuilderFactory;
    private StepBuilderFactory stepBuilderFactory;
    private TicketBookingRepository repository;


  // FlatFileItemReader class  is used to read the information from source
    @Bean
    public FlatFileItemReader<TicketBooking> reader(){

        FlatFileItemReader<TicketBooking> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(new FileSystemResource("src/main/resources/bookings.csv"));
        itemReader.setName("csv Reader");
        itemReader.setLinesToSkip(1);
        itemReader.setLineMapper(lineMapper());

        return itemReader;

    }


    // lineMapper - used to tell LineMapper that how to read csv file and how to map the data from csv file to
    // the TicketBooking object
    private LineMapper<TicketBooking> lineMapper() {

        DefaultLineMapper<TicketBooking> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("id","firstname","lastname","email","tolocation","fromlocation","date","time","price","busno");

        BeanWrapperFieldSetMapper<TicketBooking> fieldSetMapper= new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(TicketBooking.class);


        Map<Class<?>, CustomDateEditor> customEditors = new HashMap<>();
        customEditors.put(Date.class, new CustomDateEditor(new SimpleDateFormat("yyMMdd"), false));
        fieldSetMapper.setCustomEditors(customEditors);



        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;

    }


    @Bean
    public TicketBookingProcessor processor(){
        return new TicketBookingProcessor();
    }

    @Bean
    public RepositoryItemWriter<TicketBooking> writer(){

        RepositoryItemWriter <TicketBooking>  writer = new RepositoryItemWriter<>();
        writer.setRepository(repository);
        writer.setMethodName("save");

        return writer;

    }


    @Bean
    public Step step1(){
        return stepBuilderFactory.get("csv-step").<TicketBooking,TicketBooking>chunk(10)
               .reader(reader())
               .processor(processor())
               .writer(writer())
                .taskExecutor(taskExecutor())
               .build();
    }

    @Bean
    public Job runjob(){

        return jobBuilderFactory.get("importPassenegers")
                .flow(step1()).end().build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor();
        asyncTaskExecutor.setConcurrencyLimit(10);
        return asyncTaskExecutor;
    }





}

