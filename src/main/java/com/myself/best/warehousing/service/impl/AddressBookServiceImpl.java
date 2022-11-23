package com.myself.best.warehousing.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myself.best.warehousing.entity.AddressBook;
import com.myself.best.warehousing.mapper.AddressBookMapper;
import com.myself.best.warehousing.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {

}
