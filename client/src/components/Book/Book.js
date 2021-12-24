import React, { Component } from 'react'

export default class Book extends Component {
    constructor(props) {
        super(props)
    }

    render() {
        return (
            <tr>
                <td>{this.props.book.isbn}</td>
                <td>{this.props.book.title}</td>
                <td>{this.props.book.authors}</td>
                <th><button className='darkButton'>Add</button></th>
            </tr>
        )
    }
}
