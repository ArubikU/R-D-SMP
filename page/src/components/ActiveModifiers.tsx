import React from 'react';
import { SectionTitle } from './ui/SectionTitle';
import { EventCard } from './EventCard';
import { dailyEvents, activeModifiersConfig } from '../data';

export const ActiveModifiers: React.FC = () => {
    // Filter events based on the configuration list
    const activeEvents = dailyEvents.filter(event => 
        activeModifiersConfig.includes(event.name)
    );

    return (
        <div className="animate-in fade-in duration-500">
            <SectionTitle>Modificadores Activos</SectionTitle>
            <p className="text-gray-400 mb-8 text-lg">
                Estos son los efectos que están actualmente activos en el servidor.
                {activeEvents.length === 0 && " No hay modificadores activos por el momento."}
            </p>

            {activeEvents.length > 0 ? (
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                    {activeEvents.map((ev, idx) => (
                        <EventCard key={idx} event={ev} />
                    ))}
                </div>
            ) : (
                <div className="text-center py-12 border-2 border-dashed border-gray-800 rounded-lg">
                    <span className="text-4xl block mb-2">💤</span>
                    <p className="text-gray-500">El servidor está tranquilo... por ahora.</p>
                </div>
            )}
        </div>
    );
};
