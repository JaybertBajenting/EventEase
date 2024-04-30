'use client';

import Button from '@mui/material/Button';
import Modal from '@mui/material/Modal';
import React, { useState, useRef } from 'react';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import { styled } from '@mui/joy';

const VisuallyHiddenInput = styled('input')`
  clip: rect(0 0 0 0);
  clip-path: inset(50%);
  height: 1px;
  overflow: hidden;
  position: absolute;
  bottom: 0;
  left: 0;
  white-space: nowrap;
  width: 1px;
`;

const CreateEvent = ({ visible, onClose }) => {
  
  const [showCalendar, setShowCalendar] = useState(false);
  const [formData, setFormData] = useState({ 
    eventName: "",
    typeofEvent: "One-Time",
    eventDescription: "",
    startDate: null, 
    startTime: null, 
    endDate: null, 
    endTime: null, 
    uploadedPhoto: null,
  });
  const fileInputRef = useRef(null);

  const handleInputChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleTypeofEventChange = (e) => {
    setFormData({
      ...formData,
      typeofEvent: e.target.value
    });
  };
 

  const handleDateChange = (date, type) => {
    if (type === 'start') {
      setFormData({
        ...formData,
        startDate: date
      });
    } else {
      setFormData({
        ...formData,
        endDate: date
      });
    }
  };

  const handleTimeChange = (time, type) => {
    if (type === 'start') {
      setFormData({
        ...formData,
        startTime: time
      });
    } else {
      setFormData({
        ...formData,
        endTime: time
      });
    }
  };

  const toggleCalendar = (type) => {
    if (showCalendar === type) {
      setShowCalendar(false);
    } else {
      setShowCalendar(type);
    }
  };
  const handleFileChange = (e) => {
    const file = e.target.files[0];
    // Perform additional checks if needed, like file type validation
    setFormData({
      ...formData,
      uploadedPhoto: file,
    });
  };
  const handleButtonClick = () => {
    fileInputRef.current.click(); // Programmatically trigger the file input field
  };
  return (
    <Modal
      open={visible}
      onClose={onClose}
    >
      <div 
        className='bg-white p-4 rounded-3xl mt-20 ml-[20rem] w-[48rem] h-[25rem] relative' 
        style={{ backgroundImage: "url('/inno.png')", backgroundSize: 'cover' }}
      >
        <h2 className='text-lg font-bold -mt-4 p-4'>Create Event</h2>
       
        <div className="grid grid-cols-1 gap-5">
          <div className="relative p-5 -mt-1">
            <p className="font-poppins text-sm font-regular -mt-6">Event Name <span className="text-red-800">*</span></p>
            <input 
              type='text' 
              placeholder='Enter Text' 
              name="eventName" 
              value={formData.eventName} 
              onChange={handleInputChange} 
              className="p-2 w-[20rem] h-[32px] rounded-2xl border-[1.5px] border-black text-[12px]"
            />
          </div>

          <div className="relative p-5 -mt-7">
            <p className="font-poppins text-sm font-regular -mt-6">Type of Event<span className="text-red-800">*</span></p>
            <select
              value={formData.typeofEvent}
              onChange={handleTypeofEventChange}
              className="p-2 w-[20rem] h-[32px] rounded-2xl border-[1.5px] border-black text-[12px]" 
             
            >
              <option value="One-Time">One-Time</option>
              <option value="Series">Series</option>
            </select>
          </div>

          <div className="relative p-5 -mt-7">
            <p className="font-poppins text-sm font-regular -mt-6">Event Description <span className="text-red-800">*</span></p>
            <textarea 
              placeholder='Enter Description' 
              name="eventDescription" 
              value={formData.eventDescription} 
              onChange={handleInputChange} 
              className="p-2 w-[20rem] h-[60px] rounded-2xl border-[1.5px] border-black resize-none text-[12px]"
            />
          </div>
        
           <div className="relative p-5 -mt-8">
            <p className="font-poppins text-sm font-regular -mt-6">Start Date <span className="text-red-800">*</span></p>
            <div className="relative">
              <input 
                type='text' 
                placeholder='Select Date and Time' 
                value={
                  formData.startDate && formData.startTime
                    ? `${formData.startDate.toLocaleDateString()} ${formData.startTime.toLocaleTimeString()}`
                    : ''
                }
                readOnly
                className="p-1 w-[9rem] h-[32px] rounded-2xl border-[1.5px] border-black text-[10px]"
              />
              <img 
                src="/calendar.png"
                alt="Calendar" 
                className="absolute top-0 right-20 m-2 cursor-pointer w-[15px] mr-[30rem]"
                onClick={() => toggleCalendar('start')}
              />
              {showCalendar === 'start' && (
                <div className="absolute top-full w-[5rem] h-[32px] -left-1 -mt-3 p-4">
                  <DatePicker
                    selected={formData.startDate}
                    onChange={(date) => handleDateChange(date, 'start')}
                    dateFormat="MM/dd/yyyy"
                    className="border-[1px] border-black mt-[5px] w-[7rem] font-regular text-[10px] rounded-2xl text-center"
                  />
                  <DatePicker
                    selected={formData.startTime}
                    onChange={(time) => handleTimeChange(time, 'start')}
                    showTimeSelect
                    showTimeSelectOnly
                    timeIntervals={1}
                    timeCaption="Time"
                    dateFormat="h:mm aa"
                    className="border-[1px] border-black mt-[5px] w-[7rem] font-regular text-[10px] rounded-2xl text-center"
                  />
                </div>
              )}
            </div>
          </div>
         
          <div className="relative p-5 -mt-[5.5rem] ml-[11rem]">
            <p className="font-poppins text-sm font-regular -mt-6">End Date <span className="text-red-800">*</span></p>
            <div className="relative">
              <input 
                type='text' 
                placeholder='Select Date and Time' 
                value={
                  formData.endDate && formData.endTime
                    ? `${formData.endDate.toLocaleDateString()} ${formData.endTime.toLocaleTimeString()}`
                    : ''
                }
                readOnly
                className="p-1 w-[9rem] h-[32px] rounded-2xl border-[1.5px] border-black text-[10px]"
              />
              <img 
                src="/calendar.png"
                alt="Calendar" 
                className="absolute top-0 right-[254px] m-2 cursor-pointer w-[15px] mr-[8rem]"
                onClick={() => toggleCalendar('end')}
              />
              {showCalendar === 'end' && (
                <div className="absolute top-full w-[5rem] h-[32px] -left-1 -mt-3 p-4">
                  <DatePicker
                    selected={formData.endDate}
                    onChange={(date) => handleDateChange(date, 'end')}
                    dateFormat="MM/dd/yyyy"
                    className="border-[1px] border-black mt-[5px] w-[7rem] font-regular text-[10px] rounded-2xl text-center"
                  />
                  <DatePicker
                    selected={formData.endTime}
                    onChange={(time) => handleTimeChange(time, 'end')}
                    showTimeSelect
                    showTimeSelectOnly
                    timeIntervals={1}
                    timeCaption="Time"
                    dateFormat="h:mm aa"
                    className="border-[1px] border-black mt-[5px] w-[7rem] font-regular text-[10px] rounded-2xl text-center"
                  />
                </div>
              )}
            </div>
          </div>
        </div>
        <div className=' font-bold -mt-[22rem] ' >
          <Button onClick={onClose} style={{ color: 'black', fontSize:'25px', marginLeft:'43rem' }}>X</Button>
          <div className="grid grid-cols-1 gap-5">
          
          <div className="relative p-5 mt-[1rem] ml-[28.5rem] rounded-2xl border-[2px] border-customYellow" style={{ width: '12rem', height: '12rem' }}>
            <div className="relative" style={{ width: '100%', height: '100%' }}>
              <VisuallyHiddenInput
                type="file"
                accept="image/*"
                ref={fileInputRef} 
                onChange={handleFileChange}
              />
              {formData.uploadedPhoto && (
                <img 
                  src={URL.createObjectURL(formData.uploadedPhoto)} 
                  alt="Uploaded" 
                  className="absolute top-0 left-0 w-full h-full object-cover rounded " 
                  style={{ objectFit: 'cover' }} 
                />
              )}
              {!formData.uploadedPhoto && (
                <div className="absolute top-0 left-0 w-full h-full"></div>
              )}
              <Button
                component="label"
                role={undefined}
                tabIndex={-1}
                style={{ 
                  marginTop: '10.5rem', 
                  fontSize:'11px', 
                  color:'black', 
                  fontWeight:'bold', 
                  textDecoration:'underline', 
                  textTransform:'none',
                  marginRight:'-1rem',
                  outline: 'none'
                }}
                onClick={() => fileInputRef.current.click()} 
              >
              Upload Event Picture
              </Button>
                </div>
              </div>

            <div  className='ml-[37rem] h-[2rem] mt-[3rem] bg-customYellow rounded-xl w-[6rem] text-center textcolor-white'>
                <Button style={{ color: 'black', fontWeight:'semibold',fontSize:'14px', outline: 'none'  }}>CREATE</Button>
            </div>

        </div>
        </div>
      </div>
    </Modal>
  );
};

export default CreateEvent;
